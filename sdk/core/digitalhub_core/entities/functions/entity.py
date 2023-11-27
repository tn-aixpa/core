"""
Function module.
"""
from __future__ import annotations

import typing
from concurrent.futures import ThreadPoolExecutor

from digitalhub_core.context.builder import get_context
from digitalhub_core.entities._base.entity import Entity
from digitalhub_core.entities._builders.metadata import build_metadata
from digitalhub_core.entities._builders.spec import build_spec
from digitalhub_core.entities._builders.status import build_status
from digitalhub_core.entities.tasks.crud import create_task, delete_task, new_task
from digitalhub_core.utils.api import api_ctx_create, api_ctx_update
from digitalhub_core.utils.commons import FUNC
from digitalhub_core.utils.exceptions import EntityError
from digitalhub_core.utils.generic_utils import build_uuid, get_timestamp
from digitalhub_core.utils.io_utils import write_yaml

if typing.TYPE_CHECKING:
    from digitalhub_core.context.context import Context
    from digitalhub_core.entities.functions.metadata import FunctionMetadata
    from digitalhub_core.entities.functions.spec import FunctionSpec
    from digitalhub_core.entities.functions.status import FunctionStatus
    from digitalhub_core.entities.runs.entity import Run
    from digitalhub_core.entities.tasks.entity import Task


class Function(Entity):
    """
    A class representing a function.
    """

    def __init__(
        self,
        project: str,
        name: str,
        uuid: str,
        kind: str,
        metadata: FunctionMetadata,
        spec: FunctionSpec,
        status: FunctionStatus,
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
        metadata : FunctionMetadata
            Metadata of the object.
        spec : FunctionSpec
            Specification of the object.
        status : FunctionStatus
            Status of the object.
        """
        super().__init__()
        self.project = project
        self.name = name
        self.id = uuid
        self.kind = kind
        self.metadata = metadata
        self.spec = spec
        self.status = status

        # Private attributes
        self._tasks: dict[str, Task] = {}
        self._obj_attr.extend(["project", "name"])

    #############################
    #  Save / Export
    #############################

    def save(self, update: bool = False) -> dict:
        """
        Save function into backend.

        Parameters
        ----------
        uuid : str
            Specify uuid for the function update

        Returns
        -------
        dict
            Mapping representation of Function from backend.
        """
        obj = self.to_dict(include_all_non_private=True)

        if not update:
            api = api_ctx_create(self.project, FUNC)
            return self._context().create_object(obj, api)

        self.metadata.updated = obj["metadata"]["updated"] = get_timestamp()
        api = api_ctx_update(self.project, FUNC, self.name, self.id)
        return self._context().update_object(obj, api)

    def export(self, filename: str | None = None) -> None:
        """
        Export object as a YAML file.

        Parameters
        ----------
        filename : str
            Name of the export YAML file. If not specified, the default value is used.

        Returns
        -------
        None
        """
        obj = self.to_dict()
        filename = filename if filename is not None else f"function_{self.project}_{self.name}.yaml"
        write_yaml(filename, obj)

    #############################
    #  Context
    #############################

    def _context(self) -> Context:
        """
        Get context.

        Returns
        -------
        Context
            Context.
        """
        return get_context(self.project)

    #############################
    #  Function Methods
    #############################

    def run(
        self,
        action: str,
        inputs: dict | None = None,
        outputs: dict | None = None,
        parameters: dict | None = None,
        local_execution: bool = False,
        **kwargs,
    ) -> Run:
        """
        Run function.

        Parameters
        ----------
        action : str
            Action to execute. Task parameter.
        inputs : dict
            Function inputs. Run parameter.
        outputs : dict
            Function outputs. Run parameter.
        parameters : dict
            Function parameters. Run parameter.
        local_execution : bool
            Flag to determine if object has local execution. Run parameter.
        **kwargs
            Keyword arguments passed to Task builder.
        Returns
        -------
        Run
            Run instance.
        """

        # Create task if does not exists
        task = self._tasks.get(action)
        if task is None:
            task = self.new_task(kind=action, **kwargs)

        # Run function from task
        run = task.run(inputs, outputs, parameters, local_execution)

        # If execution is done by backend,
        # save run and return the object
        if not local_execution:
            run.save()
            return run

        # If local execution, build, save and launch run
        run.build()
        run.save()
        with ThreadPoolExecutor(max_workers=1) as executor:
            result = executor.submit(run.run)
        return result.result()

    def _get_function_string(self) -> str:
        """
        Get function string.

        Returns
        -------
        str
            Function string.
        """
        return f"{self.kind}://{self.project}/{self.name}:{self.id}"

    #############################
    #  CRUD Methods for Tasks
    #############################

    def new_task(self, **kwargs) -> Task:
        """
        Create new task.

        Parameters
        ----------
        **kwargs
            Keyword arguments.

        Returns
        -------
        Task
            New task.
        """
        kwargs["project"] = self.project
        kwargs["function"] = self._get_function_string()
        task = new_task(**kwargs)
        self._tasks[kwargs["kind"]] = task
        return task

    def update_task(self, kind: str, **kwargs) -> None:
        """
        Update task.

        Parameters
        ----------
        kind : str
            Kind of the task.
        **kwargs
            Keyword arguments.

        Returns
        -------
        None

        Raises
        ------
        EntityError
            If task does not exist.
        """
        self._raise_if_not_exists(kind)

        # Update kwargs
        kwargs["project"] = self.project
        kwargs["kind"] = kind
        kwargs["function"] = self._get_function_string()
        kwargs["uuid"] = self._tasks[kind].id

        # Update task
        task = create_task(**kwargs)
        task.save(update=True)
        self._tasks[kind] = task

    def get_task(self, kind: str) -> Task:
        """
        Get task.

        Parameters
        ----------
        kind : str
            Kind of the task.

        Returns
        -------
        Task
            Task.

        Raises
        ------
        EntityError
            If task is not created.
        """
        self._raise_if_not_exists(kind)
        return self._tasks[kind]

    def delete_task(self, kind: str) -> None:
        """
        Delete task.

        Parameters
        ----------
        kind : str
            Kind of the task.

        Returns
        -------
        None

        Raises
        ------
        EntityError
            If task is not created.
        """
        self._raise_if_not_exists(kind)
        delete_task(self.project, self._tasks[kind].name)
        self._tasks[kind] = None

    def _raise_if_not_exists(self, kind: str) -> None:
        """
        Raise error if task is not created.

        Parameters
        ----------
        kind : str
            Kind of the task.

        Returns
        -------
        None

        Raises
        ------
        EntityError
            If task does not exist.
        """
        if self._tasks.get(kind) is None:
            raise EntityError("Task does not exist.")

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
        project = obj.get("project")
        name = obj.get("name")
        kind = obj.get("kind")
        uuid = build_uuid(obj.get("id"))
        metadata = build_metadata(entity, **obj.get("metadata"))
        spec = build_spec(entity, kind, ignore_validation=True, module_kind=kind, **obj.get("spec"))
        status = build_status(entity, **obj.get("status"))
        return {
            "project": project,
            "name": name,
            "uuid": uuid,
            "kind": kind,
            "metadata": metadata,
            "spec": spec,
            "status": status,
        }


def function_from_parameters(
    project: str,
    name: str,
    kind: str,
    uuid: str | None = None,
    description: str | None = None,
    embedded: bool = True,
    source: str | None = None,
    **kwargs,
) -> Function:
    """
    Create a new Function instance and persist it to the backend.

    Parameters
    ----------
    project : str
        Name of the project.
    name : str
        Identifier of the function.
    kind : str
        The type of the function.
    uuid : str
        UUID.
    description : str
        Description of the function.
    embedded : bool
        Flag to determine if object must be embedded in project.
    source : str
        Path to the function's source code on the local file system.
    **kwargs
        Keyword arguments.

    Returns
    -------
    Function
       Object instance.
    """
    uuid = build_uuid(uuid)
    spec = build_spec(
        FUNC,
        kind,
        module_kind=kind,
        source=source,
        **kwargs,
    )
    metadata = build_metadata(
        FUNC,
        project=project,
        name=name,
        version=uuid,
        description=description,
        embedded=embedded,
    )
    status = build_status(FUNC)
    return Function(
        project=project,
        name=name,
        uuid=uuid,
        kind=kind,
        metadata=metadata,
        spec=spec,
        status=status,
    )


def function_from_dict(obj: dict) -> Function:
    """
    Create function from dictionary.

    Parameters
    ----------
    obj : dict
        Dictionary to create function from.

    Returns
    -------
    Function
        Function object.
    """
    return Function.from_dict(FUNC, obj)
