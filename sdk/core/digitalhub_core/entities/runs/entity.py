"""
Run module.
"""
from __future__ import annotations

import typing
from collections import namedtuple

from digitalhub_core.context.builder import get_context
from digitalhub_core.entities._base.entity import Entity
from digitalhub_core.entities._base.status import State
from digitalhub_core.entities._builders.metadata import build_metadata
from digitalhub_core.entities._builders.spec import build_spec
from digitalhub_core.entities._builders.status import build_status
from digitalhub_core.entities.artifacts.crud import get_artifact_from_key
from digitalhub_core.entities.dataitems.crud import get_dataitem_from_key
from digitalhub_core.runtimes.builder import build_runtime
from digitalhub_core.utils.api import api_base_create, api_base_read, api_base_update, api_ctx_read
from digitalhub_core.utils.commons import ARTF, DTIT, FUNC, LOGS, RUNS, TASK
from digitalhub_core.utils.exceptions import EntityError
from digitalhub_core.utils.generic_utils import build_uuid, get_timestamp
from digitalhub_core.utils.io_utils import write_yaml

if typing.TYPE_CHECKING:
    from digitalhub_core.context.context import Context
    from digitalhub_core.entities.artifacts.entity import Artifact
    from digitalhub_core.entities.dataitems.entity import Dataitem
    from digitalhub_core.entities.runs.metadata import RunMetadata
    from digitalhub_core.entities.runs.spec import RunSpec
    from digitalhub_core.entities.runs.status import RunStatus
    from digitalhub_core.runtimes.base import Runtime


TaskString = namedtuple(
    "TaskString",
    ["function_kind", "task_kind", "function_name", "function_id", "task_id"],
)


class Run(Entity):
    """
    A class representing a run.
    """

    def __init__(
        self,
        project: str,
        uuid: str,
        kind: str,
        metadata: RunMetadata,
        spec: RunSpec,
        status: RunStatus,
    ) -> None:
        """
        Constructor.

        Parameters
        ----------
        project : str
            Name of the project.
        uuid : str
            UUID.
        kind : str
            The type of the run.
        metadata : RunMetadata
            Metadata of the object.
        spec : RunSpec
            Specification of the object.
        status : RunStatus
            Status of the object.
        """
        super().__init__()
        self.project = project
        self.id = uuid
        self.kind = kind
        self.metadata = metadata
        self.spec = spec
        self.status = status

        self._obj_attr.extend(["project"])

    #############################
    #  Save / Export
    #############################

    def save(self, update: bool = False) -> dict:
        """
        Save run into backend.

        Parameters
        ----------
        uuid : str
            Ignore this parameter.

        Returns
        -------
        dict
            Mapping representation of Run from backend.
        """
        obj = self.to_dict(include_all_non_private=True)

        if not update:
            api = api_base_create(RUNS)
            return self._context().create_object(obj, api)

        self.metadata.updated = obj["metadata"]["updated"] = get_timestamp()
        api = api_base_update(RUNS, self.id)
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
        filename = filename if filename is not None else f"run_{self.project}_{self.spec.task_id}_{self.id}.yaml"
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
    #  Run Methods
    #############################

    def build(self) -> None:
        """
        Build run.

        Returns
        -------
        None
        """
        function = self._get_function()
        task = self._get_task()
        runtime = self._get_runtime()
        new_spec = runtime.build(function, task, self.to_dict())
        # Insert task string validation
        self.spec = build_spec(RUNS, self.kind, ignore_validation=True, **new_spec)
        self._set_status({"state": State.BUILT.value})
        self.save(update=True)

    def run(self) -> Run:
        """
        Run run.

        Returns
        -------
        Run
            Run object.
        """
        if self.spec.local_execution:
            if not self.status.state == State.BUILT.value:
                raise EntityError("Run is not in built state. Build it again.")
            self._set_status({"state": State.RUNNING.value})
            self.save(update=True)

        runtime = self._get_runtime()
        try:
            status = runtime.run(self.to_dict(include_all_non_private=True))
        except Exception as err:
            status = {"state": State.ERROR.value, "message": str(err)}
        self._set_status(status)
        self.save(update=True)
        return self

    def refresh(self) -> Run:
        """
        Get run from backend.

        Returns
        -------
        Run
            Run object.
        """
        api = api_base_read(RUNS, self.id)
        obj = self._context().read_object(api)
        refreshed_run = self.from_dict(RUNS, obj, ignore_validation=True)
        self.kind = refreshed_run.kind
        self.metadata = refreshed_run.metadata
        self.spec = refreshed_run.spec
        self.status = refreshed_run.status
        return self

    def logs(self) -> dict:
        """
        Get run's logs from backend.
        Returns empty dictionary if context is local.

        Returns
        -------
        dict
            Logs from backend.
        """
        if self._context().local:
            return {}
        api = api_base_read(LOGS, self.id)
        return self._context().read_object(api)

    def _set_status(self, status: dict) -> None:
        """
        Set run status.

        Parameters
        ----------
        status : dict
            Status to set.

        Returns
        -------
        None

        Raises
        ------
        EntityError
            If status is not a dictionary or a Status object.
        """
        if not isinstance(status, dict):
            raise EntityError("Status must be a dictionary.")
        self.status = build_status(RUNS, **status)

    #############################
    #  Artifacts and Dataitems
    #############################

    def get_artifacts(self, output_key: str | None = None) -> Artifact | list[Artifact]:
        """
        Get artifact(s) from backend produced by the run through its key.

        Parameters
        ----------
        output_key : str, optional
            Key of the artifact to get. If not provided, returns all artifacts.

        Returns
        -------
        Artifact | list[Artifact]
            Artifact(s) from backend.
        """
        return self._get_objects(ARTF, get_artifact_from_key, output_key)

    def get_dataitems(self, output_key: str | None = None) -> Dataitem | list[Dataitem]:
        """
        Get dataitem(s) from backend produced by the run through its key.

        Parameters
        ----------
        output_key : str, optional
            Key of the dataitem to get. If not provided, returns all dataitems.

        Returns
        -------
        Dataitem | list[Dataitem]
            Dataitem(s) from backend.
        """
        return self._get_objects(DTIT, get_dataitem_from_key, output_key)

    def _get_objects(self, object_type: str, func: callable, output_key: str | None = None) -> list:
        """
        Get objects from backend produced by the run through its key.

        Parameters
        ----------
        object_type : str
            Type of the object to get.
        func : callable
            Function to get object from backend.
        output_key : str
            Key of the object to get. If not provided, returns all objects.

        Returns
        -------
        list
            Objects from backend.

        Raises
        ------
        EntityError
            If object type is not supported or if run has
            no result or if object with key is not found.
        """
        self.refresh()
        if object_type == DTIT:
            result = self.status.dataitems
        elif object_type == ARTF:
            result = self.status.artifacts
        else:
            raise EntityError(f"Object type '{object_type}' not supported.")
        if result is None:
            raise EntityError("Run has no result (maybe try when it finishes).")
        if output_key is not None:
            key = next((r.get("id") for r in result if r.get("key") == output_key), None)
            if key is None:
                raise EntityError(f"No {object_type} found with key '{output_key}'.")
            return func(key)
        return [func(r.get("id")) for r in result]

    #############################
    #  Functions and Tasks
    #############################

    def _parse_task_string(self) -> TaskString:
        """
        Parse task string.

        Returns
        -------
        None
        """
        kinds, func = self.spec.task.split("://")
        fnc_kind, tsk_kind = kinds.split("+")
        fnc_name, fnc_id = func.split("/")[1].split(":")
        return TaskString(fnc_kind, tsk_kind, fnc_name, fnc_id, self.spec.task_id)

    def _get_function(self) -> dict:
        """
        Get function from backend. Reimplemented to avoid circular imports.

        Returns
        -------
        dict
            Function from backend.
        """
        parsed = self._parse_task_string()
        api = api_ctx_read(self.project, FUNC, parsed.function_name, parsed.function_id)
        return self._context().read_object(api)

    def _get_task(self) -> dict:
        """
        Get task from backend. Reimplemented to avoid circular imports.

        Returns
        -------
        dict
            Task from backend.
        """
        parsed = self._parse_task_string()
        api = api_base_read(TASK, parsed.task_id)
        return self._context().read_object(api)

    #############################
    # Runtimes
    #############################

    def _get_runtime(self) -> Runtime:
        """
        Build runtime to build run or execute it.

        Returns
        -------
        Runtime
            Runtime object.
        """
        fnc_kind = self._parse_task_string().function_kind
        return build_runtime(fnc_kind)

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
        kind = obj.get("kind")
        uuid = build_uuid(obj.get("id"))
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
            "project": project,
            "uuid": uuid,
            "kind": kind,
            "metadata": metadata,
            "spec": spec,
            "status": status,
        }


def run_from_parameters(
    project: str,
    task: str,
    task_id: str,
    kind: str,
    uuid: str | None = None,
    inputs: dict | None = None,
    outputs: list | None = None,
    parameters: dict | None = None,
    local_execution: bool = False,
    **kwargs,
) -> Run:
    """
    Create run.

    Parameters
    ----------
    project : str
        Name of the project.
    task_id : str
        Identifier of the task associated with the run.
    task : str
        Name of the task associated with the run.
    kind : str
        The type of the run.
    uuid : str
        UUID.
    inputs : dict
        Inputs of the run.
    outputs : list
        Outputs of the run.
    parameters : dict
        Parameters of the run.
    local_execution : bool
        Flag to determine if object has local execution.
    embedded : bool
        Flag to determine if object must be embedded in project.
    **kwargs
        Keyword arguments.

    Returns
    -------
    Run
        Run object.
    """
    uuid = build_uuid(uuid)
    metadata = build_metadata(
        RUNS,
        project=project,
        name=uuid,
    )
    spec = build_spec(
        RUNS,
        kind,
        task=task,
        task_id=task_id,
        inputs=inputs,
        outputs=outputs,
        parameters=parameters,
        local_execution=local_execution,
        **kwargs,
    )
    status = build_status(RUNS)
    return Run(
        project=project,
        uuid=uuid,
        kind=kind,
        metadata=metadata,
        spec=spec,
        status=status,
    )


def run_from_dict(obj: dict) -> Run:
    """
    Create run from dictionary.

    Parameters
    ----------
    obj : dict
        Dictionary to create run from.

    Returns
    -------
    Run
        Run object.
    """
    return Run.from_dict(RUNS, obj)
