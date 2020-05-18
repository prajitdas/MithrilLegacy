package edu.umbc.cs.ebiquity.mithril.data.model.rules.actions;

public class RuleAction {
	private int id;
	private Action action;
	public RuleAction() {
		super();
		this.id = -1;
		this.action = Action.DENY;
	}
	public RuleAction(Action action) {
		super();
		this.id = -1;
		this.action = action;
	}
	public RuleAction(int actionId, Action action) {
		super();
		this.id = actionId;
		this.action = action;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RuleAction other = (RuleAction) obj;
		if (action != other.action)
			return false;
		if (id != other.id)
			return false;
		return true;
	}
	public Action getAction() {
		return action;
	}
	public int getId() {
		return id;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result + id;
		return result;
	}
	public void setAction(Action action) {
		this.action = action;
	}
	public void setId(int id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return "RuleAction [id=" + id + ", action=" + action + "]";
	}
}