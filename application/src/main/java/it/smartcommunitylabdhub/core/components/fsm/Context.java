package it.smartcommunitylabdhub.core.components.fsm;

import java.io.Serializable;
import java.util.*;

class Context<C> implements Serializable {
	private Optional<C> value;

	public Context(Optional<C> data) {
		this.value = data;
	}

	public Optional<C> getValue() {
		return value;
	}

	public void setValue(Optional<C> data) {
		this.value = data;
	}
}
