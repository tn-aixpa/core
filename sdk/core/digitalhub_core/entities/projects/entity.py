"""
Project module.
"""
from __future__ import annotations

import typing
from typing import Callable, TypeVar

from digitalhub_core.client.builder import get_client
from digitalhub_core.context.builder import set_context
from digitalhub_core.entities._base.entity import Entity
from digitalhub_core.entities._builders.metadata import build_metadata
from digitalhub_core.entities._builders.spec import build_spec
from digitalhub_core.entities._builders.status import build_status
from digitalhub_core.entities.artifacts.crud import (
    create_artifact_from_dict,
    delete_artifact,
    get_artifact,
    new_artifact,
)
from digitalhub_core.entities.dataitems.crud import (
    create_dataitem_from_dict,
    delete_dataitem,
    get_dataitem,
    new_dataitem,
)
from digitalhub_core.entities.functions.crud import (
    create_function_from_dict,
    delete_function,
    get_function,
    new_function,
)
from digitalhub_core.entities.workflows.crud import (
    create_workflow_from_dict,
    delete_workflow,
    get_workflow,
    new_workflow,
)
from digitalhub_core.utils.api import api_base_create, api_base_read, api_base_update
from digitalhub_core.utils.commons import ARTF, DTIT, FUNC, PROJ, WKFL
from digitalhub_core.utils.exceptions import BackendError, EntityError
from digitalhub_core.utils.generic_utils import build_uuid, get_timestamp
from digitalhub_core.utils.io_utils import write_yaml

if typing.TYPE_CHECKING:
    from digitalhub_core.entities.artifacts.entity import Artifact
    from digitalhub_core.entities.dataitems.entity import Dataitem
    from digitalhub_core.entities.functions.entity import Function
    from digitalhub_core.entities.projects.metadata import ProjectMetadata
    from digitalhub_core.entities.projects.spec import ProjectSpec
    from digitalhub_core.entities.projects.status import ProjectStatus
    from digitalhub_core.entities.workflows.entity import Workflow

    Entities = TypeVar("Entities", Artifact, Function, Workflow, Dataitem)

LIST = [ARTF, FUNC, WKFL, DTIT]


def constructor_from_dict(
    dto: str,
) -> Callable[[dict], Artifact | Function | Workflow | Dataitem]:
    """
    Get constructor for dto.

    Parameters
    ----------
    dto : str
        DTO.

    Returns
    -------
    Callable[[dict], Artifact | Function | Workflow | Dataitem]
        Constructor from dict.

    Raises
    ------
    EntityError
        If dto is not valid.
    """
    if dto == ARTF:
        return create_artifact_from_dict
    if dto == FUNC:
        return create_function_from_dict
    if dto == WKFL:
        return create_workflow_from_dict
    if dto == DTIT:
        return create_dataitem_from_dict
    raise EntityError(f"DTO {dto} is not valid.")


class Project(Entity):
    """
    A class representing a project.
    """

    def __init__(
        self,
        name: str,
        kind: str,
        metadata: ProjectMetadata,
        spec: ProjectSpec,
        status: ProjectStatus,
        local: bool = False,
    ) -> None:
        """
        Constructor.

        Parameters
        ----------
        project : str
            Name of the project.
        name : str
            Name of the object.
        uuid : str
            UUID.
        kind : str
            Kind of the object.
        metadata : ProjectMetadata
            Metadata of the object.
        spec : ProjectSpec
            Specification of the object.
        status : ProjectStatus
            Status of the object.
        local: bool
            If True, export locally.
        """
        super().__init__()
        self.name = name
        self.kind = kind
        self.metadata = metadata
        self.spec = spec
        self.status = status

        # Private attributes
        self._client = get_client(local)
        self._obj_attr.append("name")

        # Set context
        set_context(self)

    #############################
    #  Save / Export
    #############################

    def save(self, update: bool = False) -> dict:
        """
        Save project and context into backend.

        Parameters
        ----------
        uuid : bool
            Ignored, placed for compatibility with other objects.

        Returns
        -------
        list
            Mapping representation of Project from backend.
        """
        # Try to refresh project if local client
        if self._client.is_local():
            obj = self._refresh().to_dict()
        else:
            obj = self.to_dict()

        if not update:
            api = api_base_create(PROJ)
            return self._client.create_object(obj, api)

        self.metadata.updated = obj["metadata"]["updated"] = get_timestamp()
        api = api_base_update(PROJ, self.id)
        return self._client.update_object(obj, api)

    def export(self, filename: str | None = None) -> None:
        """
        Export object as a YAML file. If the objects are not embedded, the objects are
        exported as a YAML file.

        Parameters
        ----------
        filename : str
            Name of the export YAML file. If not specified, the default value is used.

        Returns
        -------
        None
        """
        # Try to refresh project if local client
        if self._client.is_local():
            obj = self._refresh().to_dict()
        else:
            obj = self.to_dict()

        obj = self._parse_spec(obj)

        filename = filename if filename is not None else "project.yaml"
        write_yaml(filename, obj)

        # Export objects related to project if not embedded
        for i in LIST:
            for j in self._get_objects(i):
                _obj = constructor_from_dict(i)(j)
                if not _obj.embedded:
                    _obj.export()

    def _parse_spec(self, obj: dict) -> dict:
        """
        Parse spec dictionary.

        Parameters
        ----------
        obj : dict
            Project dictionary.

        Returns
        -------
        dict
            New project dictionary.
        """
        spec = obj.get("spec", {})
        new_spec: dict[str, list] = {}
        for i in spec:
            new_spec[i] = []
            for j in spec[i]:
                if not j.get("embedded", True):
                    _dict = {k: v for k, v in j.items() if k in self._essential_attr}
                    new_spec[i].append(_dict)
                else:
                    new_spec[i].append(j)
        obj["spec"] = new_spec
        return obj

    def _refresh(self) -> "Project":
        """
        Refresh object from backend.

        Returns
        -------
        Project
            Project object.
        """
        try:
            api = api_base_read(PROJ, self.name)
            obj = self._client.read_object(api)
            return self.from_dict(PROJ, obj)
        except BackendError:
            return self

    #############################
    #  Generic operations for objects (artifacts, functions, workflows, dataitems)
    #############################

    def _add_object(self, obj: Entities, entity_type: str) -> None:
        """
        Add object to project as specification.

        Parameters
        ----------
        obj : Entity
            Object to be added to project.
        entity_type : str
            Type of object to be added to project.

        Returns
        -------
        None
        """
        self._check_entity_type(entity_type)
        attr = getattr(self.spec, entity_type, []) + [obj.to_dict()]
        setattr(self.spec, entity_type, attr)

    def _delete_object(self, name: str, entity_type: str, uuid: str | None = None) -> None:
        """
        Delete object from project.

        Parameters
        ----------
        name : str
            Name of object to be deleted.
        entity_type : str
            Type of object to be deleted.
        uuid : str
            UUID.

        Returns
        -------
        None
        """
        if uuid is None:
            attr_name = "name"
            var = name
        else:
            attr_name = "id"
            var = uuid
        self._check_entity_type(entity_type)
        spec_list = getattr(self.spec, entity_type, [])
        setattr(self.spec, entity_type, [i for i in spec_list if i.get(attr_name) != var])

    def _get_objects(self, entity_type: str) -> list[dict]:
        """
        Get dtos objects related to project.

        Parameters
        ----------
        entity_type : str
            Type of object to be retrieved.

        Returns
        -------
        list[dict]
            List of objects related to project.
        """
        self._check_entity_type(entity_type)
        return getattr(self.spec, entity_type, [])

    @staticmethod
    def _check_entity_type(entity_type: str) -> None:
        """
        Check if kind is valid.

        Parameters
        ----------
        entity_type : str
            Type of object to be checked.

        Returns
        -------
        None

        Raises
        ------
        EntityError
            If type is not valid.
        """
        if entity_type not in LIST:
            raise EntityError(f"Kind {entity_type} is not valid.")

    #############################
    #  Artifacts
    #############################

    def new_artifact(self, **kwargs) -> Artifact:
        """
        Create an instance of the Artifact class with the provided parameters.

        Parameters
        ----------
        **kwargs
            Keyword arguments.

        Returns
        -------
        Artifact
           Object instance.
        """
        kwargs["project"] = self.name
        kwargs["kind"] = "artifact"
        obj = new_artifact(**kwargs)
        self._add_object(obj, ARTF)
        return obj

    def get_artifact(self, name: str, uuid: str | None = None) -> Artifact:
        """
        Get a Artifact from backend.

        Parameters
        ----------
        name : str
            Identifier of the artifact.
        uuid : str
            Identifier of the artifact version.

        Returns
        -------
        Artifact
            Instance of Artifact class.
        """
        obj = get_artifact(
            project=self.name,
            name=name,
            uuid=uuid,
        )
        self._add_object(obj, ARTF)
        return obj

    def delete_artifact(self, name: str, uuid: str | None = None) -> None:
        """
        Delete a Artifact from project.

        Parameters
        ----------
        name : str
            Identifier of the artifact.
        uuid : str
            Identifier of the artifact version.

        Returns
        -------
        None
        """
        delete_artifact(self.name, name, uuid=uuid)
        self._delete_object(name, ARTF, uuid=uuid)

    def set_artifact(self, artifact: Artifact) -> None:
        """
        Set a Artifact.

        Parameters
        ----------
        artifact : Artifact
            Artifact to set.

        Returns
        -------
        None
        """
        self._add_object(artifact, ARTF)

    #############################
    #  Functions
    #############################

    def new_function(self, **kwargs) -> Function:
        """
        Create a Function instance with the given parameters.

        Parameters
        ----------
        **kwargs
            Keyword arguments.

        Returns
        -------
        Function
           Object instance.
        """
        kwargs["project"] = self.name
        obj = new_function(**kwargs)
        self._add_object(obj, FUNC)
        return obj

    def get_function(self, name: str, uuid: str | None = None) -> Function:
        """
        Get a Function from backend.

        Parameters
        ----------
        name : str
            Identifier of the function.
        uuid : str
            Identifier of the function version.

        Returns
        -------
        Function
            Instance of Function class.
        """
        obj = get_function(
            project=self.name,
            name=name,
            uuid=uuid,
        )
        self._add_object(obj, FUNC)
        return obj

    def delete_function(self, name: str, uuid: str | None = None) -> None:
        """
        Delete a Function from project.

        Parameters
        ----------
        name : str
            Identifier of the function.
        uuid : str
            Identifier of the function version.

        Returns
        -------
        None
        """
        delete_function(self.name, name, uuid=uuid)
        self._delete_object(name, FUNC, uuid=uuid)

    def set_function(self, function: Function) -> None:
        """
        Set a Function.

        Parameters
        ----------
        function : Function
            Function to set.

        Returns
        -------
        None
        """
        self._add_object(function, FUNC)

    #############################
    #  Workflows
    #############################

    def new_workflow(self, **kwargs) -> Workflow:
        """
        Create a new Workflow instance with the specified parameters.

        Parameters
        ----------
        **kwargs
            Keyword arguments.

        Returns
        -------
        Workflow
            An instance of the created workflow.
        """
        kwargs["project"] = self.name
        obj = new_workflow(**kwargs)
        self._add_object(obj, WKFL)
        return obj

    def get_workflow(self, name: str, uuid: str | None = None) -> Workflow:
        """
        Get a Workflow from backend.

        Parameters
        ----------
        name : str
            Identifier of the workflow.
        uuid : str
            Identifier of the workflow version.

        Returns
        -------
        Workflow
            Instance of Workflow class.
        """
        obj = get_workflow(
            project=self.name,
            name=name,
            uuid=uuid,
        )
        self._add_object(obj, WKFL)
        return obj

    def delete_workflow(self, name: str, uuid: str | None = None) -> None:
        """
        Delete a Workflow from project.

        Parameters
        ----------
        name : str
            Identifier of the workflow.
        uuid : str
            Identifier of the workflow version.

        Returns
        -------
        None
        """
        delete_workflow(self.name, name, uuid=uuid)
        self._delete_object(name, WKFL, uuid=uuid)

    def set_workflow(self, workflow: Workflow) -> None:
        """
        Set a Workflow.

        Parameters
        ----------
        workflow : Workflow
            Workflow to set.

        Returns
        -------
        None
        """
        self._add_object(workflow, WKFL)

    #############################
    #  Dataitems
    #############################

    def new_dataitem(self, **kwargs) -> Dataitem:
        """
        Create a Dataitem.

        Parameters
        ----------
        **kwargs
            Keyword arguments.

        Returns
        -------
        Dataitem
           Object instance.
        """
        kwargs["project"] = self.name
        kwargs["kind"] = "dataitem"
        obj = new_dataitem(**kwargs)
        self._add_object(obj, DTIT)
        return obj

    def get_dataitem(self, name: str, uuid: str | None = None) -> Dataitem:
        """
        Get a Dataitem from backend.

        Parameters
        ----------
        name : str
            Identifier of the dataitem.
        uuid : str
            Identifier of the dataitem version.

        Returns
        -------
        Dataitem
            Instance of Dataitem class.
        """
        obj = get_dataitem(
            project=self.name,
            name=name,
            uuid=uuid,
        )
        self._add_object(obj, DTIT)
        return obj

    def delete_dataitem(self, name: str, uuid: str | None = None) -> None:
        """
        Delete a Dataitem from project.

        Parameters
        ----------
        name : str
            Identifier of the dataitem.
        uuid : str
            Identifier of the dataitem version.

        Returns
        -------
        None
        """
        delete_dataitem(self.name, name, uuid=uuid)
        self._delete_object(name, DTIT, uuid=uuid)

    def set_dataitem(self, dataitem: Dataitem) -> None:
        """
        Set a Dataitem.

        Parameters
        ----------
        dataitem : Dataitem
            Dataitem to set.

        Returns
        -------
        None
        """
        self._add_object(dataitem, DTIT)

    #############################
    #  Static interface methods
    #############################

    @staticmethod
    def _parse_dict(
        entity: str,
        obj: dict,
        ignore_validation: bool = False,
        module_kind: str | None = None,
    ) -> dict:
        """
        Get dictionary and parse it to a valid entity dictionary.

        Parameters
        ----------
        entity : str
            Entity type.
        obj : dict
            Dictionary to parse.

        Returns
        -------
        dict
            A dictionary containing the attributes of the entity instance.
        """
        name = build_uuid(obj.get("name"))
        kind = obj.get("kind")
        metadata = build_metadata(entity, **obj.get("metadata"))
        spec = build_spec(
            entity,
            kind,
            ignore_validation=ignore_validation,
            module_kind=module_kind,
            **obj.get("spec"),
        )
        status = build_status(entity, **obj.get("status"))
        return {
            "name": name,
            "kind": kind,
            "metadata": metadata,
            "spec": spec,
            "status": status,
        }


def project_from_parameters(
    name: str,
    kind: str,
    description: str | None = None,
    local: bool = False,
    context: str = "",
    source: str = "",
    **kwargs,
) -> Project:
    """
    Create project.

    Parameters
    ----------
    name : str
        Identifier of the project.
    kind : str
        The type of the project.
    description : str
        Description of the project.
    local : bool
        Flag to determine if object will be exported to backend.
    context : str
        The context of the project.
    source : str
        The source of the project.
    **kwargs
        Keyword arguments.

    Returns
    -------
    Project
        Project object.
    """
    name = build_uuid(name)
    spec = build_spec(
        PROJ,
        kind,
        context=context,
        source=source,
        **kwargs,
    )
    metadata = build_metadata(
        PROJ,
        name=name,
        description=description,
    )
    status = build_status(PROJ)
    return Project(
        name=name,
        kind=kind,
        metadata=metadata,
        spec=spec,
        status=status,
        local=local,
    )


def project_from_dict(obj: dict) -> Project:
    """
    Create project from dictionary.

    Parameters
    ----------
    obj : dict
        Dictionary to create project from.

    Returns
    -------
    Project
        Project object.
    """
    return Project.from_dict(PROJ, obj)
