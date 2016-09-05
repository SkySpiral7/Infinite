package com.github.SkySpiral7.Java.serialization;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.github.SkySpiral7.Java.util.ClassUtil;

public class ObjectReaderRegistry
{
   private final Map<String, Object> registry = new HashMap<>();
   private String unclaimedId;

   public void registerObject(final String id, final Object instance)
   {
      Objects.requireNonNull(id);
      Objects.requireNonNull(instance);
      registry.put(id, instance);
   }

   public <T> T getRegisteredObject(final String id)
   {
      Objects.requireNonNull(id);
      return ClassUtil.cast(registry.get(id));
   }

   /**
    * This method reads an id then returns an object registered with that id.
    * If there is no object with that id then the id becomes unclaimed and null is returned.
    * Note that there can only be 1 unclaimed id at a time.
    */
   public <T> T readObjectOrId(final ObjectStreamReader reader)
   {
      Objects.requireNonNull(reader);
      final String id = reader.readObject(String.class);
      if (registry.containsKey(id)) return ClassUtil.cast(registry.get(id));
      unclaimedId = id;
      return null;
   }

   /**
    * Call this method to associate the most recent generated id to the instance given
    * so that the same instance can be referenced again while reading.
    *
    * @throws NullPointerException
    *       if there is no id to claim or if input is null
    * @see GenerateId
    */
   public void claimId(final Object input)
   {
      Objects.requireNonNull(input);
      Objects.requireNonNull(unclaimedId);
      //TODO: if root element will throw NPE
      registry.put(unclaimedId, input);
      unclaimedId = null;
   }
}
