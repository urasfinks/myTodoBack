package ru.jamsys.database;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DatabaseScriptArgumentImpl implements DatabaseScriptArgument {

	final String name;
	final DatabaseArgumentType type;
	final DatabaseArgumentDirection direction;
	final Object value;
	final Boolean dynamicValue;
	final List<Integer> indexes;

	public DatabaseScriptArgumentImpl(String name, DatabaseArgumentType type, DatabaseArgumentDirection direction, Object value, Boolean dynamicValue) {
		this.name = name;
		this.type = type;
		this.direction = direction;
		this.value = value;
		this.dynamicValue = dynamicValue != null ? dynamicValue : false;
		this.indexes = new ArrayList<>();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Object getValue() {
		if(this.type == DatabaseArgumentType.VARCHAR){ //BigDecimal toString from int return "3.0"
			return this.value.toString();
		}
		return this.value;
	}

	@Override
	public DatabaseArgumentType getType() {
		return type;
	}

	@Override
	public DatabaseArgumentDirection getDirection() {
		return direction;
	}

	@Override
	public Boolean isDynamicValue() {
		return dynamicValue;
	}

	@Override
	public List<Integer> getIndexes() {
		return indexes;
	}

	@Override
	public void registerIndex(int index) {
		this.indexes.add(index);
	}

	@Override
	public String toString() {
		return "{\"Class\": \"" + this.getClass().getCanonicalName() + "\", \"Name\": \"" + name + "\", \"Index\": " + Stream.of(indexes).map(Object::toString).collect(Collectors.joining(", ")) + ", \"Type\": \"" + type + "\", \"Direction\": \"" + direction + "\", \"Value\": " + this.value + "}";
	}

}
