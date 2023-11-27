"""
Abstract entity module.
"""
from __future__ import annotations

from abc import ABCMeta, abstractmethod

from digitalhub_core.entities._base.base import ModelObj


class Entity(ModelObj, metaclass=ABCMeta):
    """
    Abstract class for entities.
    """

    def __init__(self) -> None:
        """
        Constructor.
        """
        # Attributes to render as dict
        self._obj_attr = ["id", "kind", "metadata", "spec", "status"]
        self._essential_attr = ["id", "kind", "name"]

    @abstractmethod
    def save(self, update: bool = False) -> dict:
        """
        Abstract save method.
        """

    @abstractmethod
    def export(self, filename: str | None = None) -> None:
        """
        Abstract save method.
        """

    def to_dict(self, include_all_non_private: bool = False) -> dict:
        """
        Return object as dict with all keys.

        Parameters
        ----------
        include_all_non_private : bool
            Whether to include all non-private attributes. If False, only
            attributes in the _obj_attr list will be included.

        Returns
        -------
        dict
            A dictionary containing the attributes of the entity instance.
        """
        dict_ = super().to_dict()
        if include_all_non_private:
            return dict_
        return {k: v for k, v in dict_.items() if k in self._obj_attr}

    def to_dict_essential(self) -> dict:
        """
        Return object as dict with some attributes.

        Returns
        -------
        dict
            A dictionary containing the attributes of the entity instance.
        """
        dict_ = super().to_dict()
        return {k: v for k, v in dict_.items() if k in self._essential_attr}

    @classmethod
    def from_dict(
        cls,
        entity: str,
        obj: dict,
        ignore_validation: bool = False,
        module_kind: str | None = None,
    ) -> "Entity":
        """
        Create object instance from a dictionary.

        Parameters
        ----------
        entity : str
            Entity type.
        obj : dict
            Dictionary to create object from.
        ignore_validation : bool
            Flag to indicate if arguments validation must be ignored.
        module_kind : str
            The module to import (for some objects).

        Returns
        -------
        Self
            Self instance.
        """
        parsed_dict = cls._parse_dict(
            entity,
            obj,
            ignore_validation=ignore_validation,
            module_kind=module_kind,
        )
        return cls(**parsed_dict)

    @staticmethod
    @abstractmethod
    def _parse_dict(
        entity: str,
        obj: dict,
        ignore_validation: bool = False,
        module_kind: str | None = None,
    ) -> dict:
        """
        Get dictionary and parse it to a valid entity dictionary.
        """

    def __repr__(self) -> str:
        """
        Return string representation of the entity object.

        Returns
        -------
        str
            A string representing the entity instance.
        """
        return str(self.to_dict(include_all_non_private=True))
