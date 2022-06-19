package ru.jamsys.database;

import java.util.List;

public interface DatabaseScriptArgument {

	String getName();

	Object getValue();

	DatabaseArgumentType getType();

	DatabaseArgumentDirection getDirection();

	Boolean isDynamicValue();

	List<Integer> getIndexes();

	void registerIndex(int index);

}
