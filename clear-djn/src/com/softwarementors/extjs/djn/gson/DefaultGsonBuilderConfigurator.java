/*
 * Copyright © 2008, 2012 Pedro Agulló Soliveres.
 * 
 * This file is part of DirectJNgine.
 *
 * DirectJNgine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 * 
 * Commercial use is permitted to the extent that the code/component(s)
 * do NOT become part of another Open Source or Commercially developed
 * licensed development library or toolkit without explicit permission.
 *
 * DirectJNgine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with DirectJNgine.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * This software uses the ExtJs library (http://extjs.com), which is 
 * distributed under the GPL v3 license (see http://extjs.com/license).
 */

package com.softwarementors.extjs.djn.gson;

import java.lang.reflect.Type;
import java.util.Date;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.softwarementors.extjs.djn.config.GlobalConfiguration;

public class DefaultGsonBuilderConfigurator implements GsonBuilderConfigurator {

  private static class DateDeserializer implements JsonDeserializer<Date> {
    public Date deserialize( JsonElement json, Type typeOfT,
        JsonDeserializationContext context)
    throws JsonParseException
    {
      assert json != null;
      assert context != null;
      assert typeOfT != null;

      if( !json.isJsonPrimitive() ) {
        throw new JsonParseException( "The value for a Date must be a valid number");
      }
      
      assert json instanceof JsonPrimitive; // To please Findugs!
      JsonPrimitive primitivejson = (JsonPrimitive)json;
      if( !primitivejson.isNumber()) {
        throw new JsonParseException( "The value for a Date must be a valid number");
      }
      return new Date( primitivejson.getAsLong() );
    }
  }

  private static class DateSerializer implements JsonSerializer<Date> {
    public JsonElement serialize( Date src, Type typeOfSrc,
        JsonSerializationContext context) {
      assert src != null;
      assert context != null;
      assert typeOfSrc != null;

      JsonElement result = new JsonPrimitive( Long.valueOf(src.getTime()) );
      return result;
    }
  }

  public void configure(GsonBuilder builder, GlobalConfiguration configuration) {
    assert builder != null;
    assert configuration != null;

    if( configuration.getDebug() ) {
      builder.setPrettyPrinting();
    }
    builder.serializeNulls();
    builder.disableHtmlEscaping();
    
    builder.registerTypeAdapter( Date.class, new DateDeserializer());
    builder.registerTypeAdapter( Date.class, new DateSerializer());
  }

}
