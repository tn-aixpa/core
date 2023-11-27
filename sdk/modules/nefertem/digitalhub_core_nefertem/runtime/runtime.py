"""
Runtime Nefertem module.
"""
from __future__ import annotations

import os
import shutil
import typing
from pathlib import Path

import nefertem
from digitalhub_core.entities._base.status import State
from digitalhub_core.entities.artifacts.crud import new_artifact
from digitalhub_core.entities.dataitems.crud import get_dataitem
from digitalhub_core.runtimes.base import Runtime
from digitalhub_core.utils.exceptions import EntityError
from digitalhub_core.utils.logger import LOGGER

if typing.TYPE_CHECKING:
    from digitalhub_core.entities.artifacts.entity import Artifact
    from digitalhub_core.entities.dataitems.entity import Dataitem
    from nefertem.client.client import Client


####################
# Runtime
####################


class RuntimeNefertem(Runtime):
    """
    Runtime Nefertem class.
    """

    def __init__(self) -> None:
        """
        Constructor.
        """
        self.output_path = "./nefertem_run"
        self.store = {"name": "local", "store_type": "local"}

    def build(self, function: dict, task: dict, run: dict) -> dict:
        """
        Merge specs.
        """
        return {
            **function.get("spec", {}),
            **task.get("spec", {}),
            **run.get("spec", {}),
        }

    def run(self, run: dict) -> dict:
        """
        Run function.

        Returns
        -------
        dict
            Status of the executed run.
        """
        # Get action
        action = self._get_action(run)

        # Handle unknown task kind
        if action not in ["validate", "profile", "infer", "metric"]:
            msg = f"Task {action} not allowed for Nefertem runtime"
            LOGGER.error(msg)
            raise EntityError(msg)

        # Execute action
        return self.execute(action, run)

    ####################
    # Execute
    ####################

    def execute(self, action: str, run: dict) -> dict:
        """
        Execute function.

        Returns
        -------
        dict
            Status of the executed run.
        """
        # Get run specs
        LOGGER.info("Starting task.")
        spec = run.get("spec")
        project = run.get("project")

        # Get inputs and parameters
        LOGGER.info("Getting inputs and parameters.")
        inputs = self._get_inputs(spec.get("inputs", {}).get("dataitems", []), project)
        resources = self._get_resources(inputs)
        run_config = spec.get("run_config")

        # Create client
        client = nefertem.create_client(output_path=self.output_path, stores=[self.store])

        # Operation to execute
        LOGGER.info("Executing nefertem run.")

        if action == "infer":
            nt_run = self.infer(client, resources, run_config)

        elif action == "profile":
            nt_run = self.profile(client, resources, run_config)

        elif action == "validate":
            constraints = spec.get("constraints")
            error_report = spec.get("error_report")
            nt_run = self.validate(client, resources, run_config, constraints, error_report)

        elif action == "metric":
            metrics = spec.get("metrics")
            nt_run = self.metric(client, resources, run_config, metrics)

        # Upload outputs
        LOGGER.info("Uploading outputs.")
        artifacts = self._upload_outputs(nt_run, project)

        # Remove tmp folder
        LOGGER.info("Removing tmp folder.")
        self.cleanup()

        # Return run status
        LOGGER.info("Task completed, returning run status.")
        return {
            "state": State.COMPLETED.value,
            "artifacts": artifacts,
            "timing": {
                "start_time": nt_run["started"],
                "end_time": nt_run["finished"],
            },
        }

    ####################
    # INFER TASK
    ####################

    def infer(self, client: Client, resources: list[dict], run_config: dict) -> dict:
        """
        Execute infer task.

        Parameters
        ----------
        client : Client
            Nefertem client.
        resources : list[dict]
            The list of nefertem resources.
        run_config : dict
            The nefertem run configuration.

        Returns
        -------
        dict
            Nefertem run info.
        """
        with client.create_run(resources, run_config) as nt_run:
            nt_run.infer()
            nt_run.log_schema()
            nt_run.persist_schema()
        return nt_run.run_info.to_dict()

    ####################
    # PROFILE TASK
    ####################

    def profile(self, client: Client, resources: list[dict], run_config: dict) -> dict:
        """
        Execute profile task.

        Parameters
        ----------
        client : Client
            Nefertem client.
        resources : list[dict]
            The list of nefertem resources.
        run_config : dict
            The nefertem run configuration.

        Returns
        -------
        dict
            Nefertem run info.
        """
        with client.create_run(resources, run_config) as nt_run:
            nt_run.profile()
            nt_run.log_profile()
            nt_run.persist_profile()
        return nt_run.run_info.to_dict()

    ####################
    # VALIDATE TASK
    ####################

    def validate(
        self, client: Client, resources: list[dict], run_config: dict, constraints: list[dict], error_report: str
    ) -> dict:
        """
        Execute validate task.

        Parameters
        ----------
        client : Client
            Nefertem client.
        resources : list[dict]
            The list of nefertem resources.
        run_config : dict
            The nefertem run configuration.
        constraints : list[dict]
            The list of nefertem constraints.
        error_report : str
            The error report modality.

        Returns
        -------
        dict
            Nefertem run info.

        Raises
        ------
        RuntimeError
            If no constraints are given.
        """
        if constraints is None:
            msg = "Error. No constraints given."
            LOGGER.error(msg)
            raise RuntimeError(msg)
        if error_report is None:
            error_report = "partial"

        with client.create_run(resources, run_config) as nt_run:
            nt_run.validate(constraints=constraints, error_report=error_report)
            nt_run.log_report()
            nt_run.persist_report()
        return nt_run.run_info.to_dict()

    ####################
    # METRIC TASK
    ####################

    def metric(self, client: Client, resources: list[dict], run_config: dict, metrics: list[dict]) -> dict:
        """
        Execute metric task.

        Parameters
        ----------
        client : Client
            Nefertem client.
        resources : list[dict]
            The list of nefertem resources.
        run_config : dict
            The nefertem run configuration.
        metrics : list[dict]
            The list of nefertem metrics.

        Returns
        -------
        dict
            Nefertem run info.

        Raises
        ------
        RuntimeError
            If no metrics are given.
        """
        if metrics is None:
            msg = "Error. No metrics given."
            LOGGER.error(msg)
            raise RuntimeError(msg)

        with client.create_run(resources, run_config) as nt_run:
            nt_run.metric(metrics=metrics)
            nt_run.log_metric()
            nt_run.persist_metric()
        return nt_run.run_info.to_dict()

    ####################
    # Inputs
    ####################

    def _get_inputs(self, inputs: list, project: str) -> list[dict]:
        """
        Materialize inputs in postgres.

        Parameters
        ----------
        inputs : list
            The list of inputs dataitems names.
        project : str
            The project name.

        Returns
        -------
        list[dict]
            The list of inputs dataitems.
        """
        Path(f"{self.output_path}/tmp").mkdir(parents=True, exist_ok=True)

        mapper = []
        for name in inputs:
            dataitem = self._get_dataitem(name, project)
            mapper.append(self._persist_dataitem(dataitem, name))
        return mapper

    def _get_dataitem(self, name: str, project: str) -> Dataitem:
        """
        Get dataitem from backend.

        Parameters
        ----------
        name : str
            The dataitem name.
        project : str
            The project name.

        Returns
        -------
        dict
            The dataitem.

        Raises
        ------
        BackendError
            If the dataitem cannot be retrieved.
        """
        try:
            LOGGER.info(f"Getting dataitem '{name}'.")
            return get_dataitem(project, name)
        except Exception:
            msg = f"Error getting dataitem '{name}'."
            LOGGER.exception(msg)
            raise RuntimeError(msg)

    def _persist_dataitem(self, dataitem: Dataitem, name: str) -> dict:
        """
        Persist dataitem locally.

        Parameters
        ----------
        dataitem : Dataitem
            The dataitem to persist.
        name : str
            The dataitem name.

        Returns
        -------
        dict
            The dataitem path.

        Raises
        ------
        EntityError
            If the dataitem cannot be persisted.
        """
        try:
            LOGGER.info(f"Persisting dataitem '{name}' locally.")
            tmp_path = f"{self.output_path}/tmp/{name}.csv"
            dataitem.as_df().to_csv(tmp_path, sep=",", index=False)
            return {"name": name, "path": tmp_path}
        except Exception:
            msg = f"Error during dataitem '{name}' collection."
            LOGGER.exception(msg)
            raise EntityError(msg)

    def _get_resources(self, inputs: list[dict]) -> list[dict]:
        """
        Create nefertem resources.

        Parameters
        ----------
        inputs : list
            The list of inputs dataitems.

        Returns
        -------
        list[dict]
            The list of nefertem resources.
        """
        try:
            resources = []
            for i in inputs:
                res = {}
                res["name"] = i["name"]
                res["path"] = i["path"]
                res["store"] = self.store["name"]
                resources.append(res)
            return resources
        except KeyError:
            msg = "Error. Dataitem path is not given."
            LOGGER.exception(msg)
            raise EntityError(msg)

    ####################
    # Outputs
    ####################

    def _upload_outputs(self, run_info: dict, project: str) -> dict:
        """
        Upload outputs as artifacts to minio.

        Parameters
        ----------
        run_info : dict
            The run info.
        project : str
            The project name.

        Returns
        -------
        dict
            List of artifacts.
        """
        artifacts = []
        for src_path in run_info.get("output_files", []):
            # Replace _ by - in artifact name for backend compatibility
            name = Path(src_path).stem.replace("_", "-")
            art = self._create_artifact(name, project, run_info["run_id"], src_path)
            self._upload_artifact_to_minio(name, art)
            artifacts.append(
                {
                    "key": art.name,
                    "kind": "artifact",
                    "id": f"store://{art.project}/artifacts/{art.kind}/{art.name}:{art.id}",
                }
            )
        return artifacts

    @staticmethod
    def _create_artifact(name: str, project: str, run_id: str, src_path: str) -> Artifact:
        """
        Create new artifact in backend.

        Parameters
        ----------
        name : str
            The artifact name.
        project : str
            The project name.
        run_id : str
            Neferetem run id.
        src_path : str
            The artifact source local path.

        Returns
        -------
        Artifact
            The new artifact.

        Raises
        ------
        EntityError
            If the artifact cannot be created.
        """
        try:
            # Get bucket name from env and filename from path
            LOGGER.info(f"Creating artifact new artifact '{name}'.")
            dst = f"s3://{os.getenv('S3_BUCKET_NAME')}/{project}/artifacts/ntruns/{run_id}/{Path(src_path).name}"
            return new_artifact(project, name, "artifact", src_path=src_path, target_path=dst)
        except Exception:
            msg = f"Error creating artifact '{name}'."
            LOGGER.exception(msg)
            raise EntityError(msg)

    @staticmethod
    def _upload_artifact_to_minio(name: str, artifact: Artifact) -> None:
        """
        Upload artifact to minio.

        Parameters
        ----------
        name : str
            The artifact name.
        artifact : Artifact
            The artifact to upload.
        """
        try:
            LOGGER.info(f"Uploading artifact '{name}' to minio.")
            artifact.upload()
        except Exception:
            msg = f"Error uploading artifact '{name}'."
            LOGGER.exception(msg)
            raise EntityError(msg)

    ####################
    # Cleanup
    ####################

    def cleanup(self) -> None:
        """
        Cleanup after run.

        Returns
        -------
        None
        """
        shutil.rmtree(f"{self.output_path}/tmp", ignore_errors=True)
