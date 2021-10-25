package io.github.dblib;

public class Component 
{
	private String name;
	private Class<?> type;
	
	public Component(String name, Class<?> type)
	{
		this.name = name;
		this.type = type;
	}

	public String getName() 
	{
		return name;
	}

	public Class<?> getType() 
	{
		return type;
	}
}
