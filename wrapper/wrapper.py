"""
Wrapper to execute an arbitrary function.
"""
import os

import digitalhub_core as dhcore
from digitalhub_core.utils.logger import LOGGER


def main():
    """
    Main function. Get run from backend and execute function.
    """

    LOGGER.info("Getting run from backend.")
    project = dhcore.get_project(os.getenv("PROJECT_NAME"))
    run = dhcore.get_run(project.name, os.getenv("RUN_ID"))

    LOGGER.info("Executing function.")
    run.run()

    LOGGER.info("Saving run.")
    run.save(update=True)

    LOGGER.info("Done.")


if __name__ == "__main__":
    main()
