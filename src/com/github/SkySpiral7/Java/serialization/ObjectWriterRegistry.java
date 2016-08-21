package com.github.SkySpiral7.Java.serialization;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ObjectWriterRegistry
{
	private final Map<Object, String> registry = new IdentityHashMap<>();

	public String registerObject(final Object instance)
	{
		Objects.requireNonNull(instance);
		final String id = UUID.randomUUID().toString();
		registry.put(instance, id);
		return id;
	}

	public void registerObject(final String id, final Object instance)
	{
		Objects.requireNonNull(id);
		Objects.requireNonNull(instance);
		registry.put(instance, id);
	}

	public String getId(final Object instance)
	{
		Objects.requireNonNull(instance);
		return registry.get(instance);
	}

}
