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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.softwarementors.extjs.djn.ClassUtils;
import com.softwarementors.extjs.djn.CollectionUtils;
import com.softwarementors.extjs.djn.StringUtils;

import edu.umd.cs.findbugs.annotations.NonNull;

public class JsonDeserializationManager {
  private static @NonNull ThreadLocal<JsonDeserializationManager> manager = new ThreadLocal<JsonDeserializationManager>();
  private static @NonNull Set<Class<?>> manyValuedClasses = new HashSet<Class<?>>();
  
  static {
    manyValuedClasses.add( Collection.class );
  }
  
  public static void registerManyValuedClasses( Class<?> clazz, Class<?>[] otherClasses) {
    assert clazz != null;
    assert otherClasses != null;
    
    manyValuedClasses.add(clazz);
    Collections.addAll( manyValuedClasses, otherClasses );
  }
  
  JsonDeserializationManager() {
    // Avoid instantiation
  }

  private @NonNull Stack<Object> parents = new Stack<Object>();
  private @NonNull Stack<String> fields = new Stack<String>();
  private @NonNull Map<Object,Set<String>> fieldExclusions = new IdentityHashMap<Object,Set<String>>();
  private @NonNull Set<String> rootExclusions = new HashSet<String>();
  private @NonNull Set<String> rootInclusions = new HashSet<String>();
  private Object root;
  private boolean excludeManyValuedFields;
  

  public static JsonDeserializationManager getManager() {
    if( manager.get() == null ) {
      manager.set(new JsonDeserializationManager() );
    }
    return manager.get();
  }

  public void friendOnlyAccess_pushField(String name) {
    assert !StringUtils.isEmpty(name);
    
    this.fields.push(name);
  }

  public void friendOnlyAccess_popField() {
    this.fields.pop();
  }

  public void friendOnlyAccess_pushParent(Object obj) {
    assert obj != null;
    
    this.parents.push(obj);
  }

  public void friendOnlyAccess_popParent() {
    this.parents.pop();
  }
  
  public void excludeManyValuedFields() {
    this.excludeManyValuedFields = true;
  }

  public void excludeFieldPaths(String firstFieldPath, String... fieldPaths) {
    assert this.rootExclusions != null;
    assert firstFieldPath != null;
    assert fieldPaths != null;
    
    this.rootInclusions.remove(firstFieldPath);
    CollectionUtils.removeAll( this.rootInclusions, fieldPaths );
    this.rootExclusions.add( firstFieldPath );
    Collections.addAll(this.rootExclusions, fieldPaths);
  }
  
  public void includeFieldPaths(String firstFieldPath, String... fieldPaths) {
    assert this.rootInclusions != null;
    assert firstFieldPath != null;
    assert fieldPaths != null;
    
    this.rootExclusions.remove(firstFieldPath);
    CollectionUtils.removeAll( this.rootExclusions, fieldPaths );
    this.rootInclusions.add( firstFieldPath );
    Collections.addAll(this.rootInclusions, fieldPaths);
  }

  /*
  public void excludeObjectFieldPaths(Object t, String firstFieldPath, String... fieldPaths) {
    if( t == null ) {
      return;
    }
    
    assert firstFieldPath != null;
    assert fieldPaths != null;

    Set<String> exclusions = this.fieldExclusions.get(t);
    if( exclusions == null ) {
      exclusions = new HashSet<String>();
      this.fieldExclusions.put( t, exclusions );
    }
    exclusions.add(firstFieldPath);
    Collections.addAll( exclusions, fieldPaths);
  }
  */

  public boolean friendOnlyAccess_isFieldExcluded(Object value, String field) {
    assert value != null;
    assert field != null;
    
    if( isRootManyValuedFieldExcluded(value, field)) {
      return true;
    }
    
    if( this.fieldExclusions.isEmpty()) {
      return false;
    }
    
    // value is an special case: can't call hasExclusionForField
    // because it checks the parents list.
    // However, check is trivial, because if it has field, it
    // must be as is, not as a dotted path!
    if( this.fieldExclusions.containsKey(value)) {
      Set<String> ex = this.fieldExclusions.get(value);
      if( ex.contains(field)) {
        return true;
      }
    }

    for( Object obj : this.parents) {
      if( this.fieldExclusions.containsKey(obj)) {
        boolean exclude = isFieldExcludedByObjectInParentsChain(obj, field); 
        if( exclude ) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean isRootManyValuedFieldExcluded(Object value, String field) {
    assert value != null;
    
    if( !this.excludeManyValuedFields || value != this.root ) {
      return false;
    }
    
    // Need to check all public and private fields
    // -maybe in parent classes too
    Class<?> fieldType = ClassUtils.getFieldType(value.getClass(), field);
    if( fieldType == null )
      return false;
    return isManyValuedClass(fieldType);
  }

  /* package */ boolean isFieldExcludedByObjectInParentsChain(Object obj, String field) {
    assert obj != null;
    assert field != null;
    int positionInChain = this.parents.indexOf(obj);
    assert positionInChain >= 0;

    // The last object is a parent or parent of a parent & has fieldExclusions.
    // We need to check whether the last field is excluded from that object!
    StringBuilder path = new StringBuilder();
    for( int i = positionInChain; i < this.fields.size(); i++) {
      path.append( this.fields.get(i) );
      path.append( '.' );
    }
    path.append( field );
    Set<String> objectExclusions = this.fieldExclusions.get(obj);
    assert objectExclusions != null;

    boolean excluded = objectExclusions.contains(path.toString());
    return excluded;
  }

  public void friendOnlyAccess_setRoot(Object root) {
    if( root != null ) {
      this.root = root;
      this.fieldExclusions.put(root, this.rootExclusions);
    }
  }

  public void friendOnlyAccess_dispose() {
    manager.remove();
  }

  public static boolean isManyValuedClass(Class<?> clazz) {
    assert clazz != null;
    if( clazz.isArray() ) {
      return true;
    }
    for( Class<?> manyValuedClass : manyValuedClasses ) {
      if( manyValuedClass.isAssignableFrom(clazz) )
        return true;
    }
    return false;
  }

  /* Why are we not supporting excludeObjects?
   * 
   * Because excluded object MUST e to null: due to theway json is parsed by gson,
   * if you have arrived to the object, you already processed and stored its name, 
   * and then we MUST do someting with it, namely set it to null (or else,
   * there will be a parse error.
   * Now, Gson might remove this object or not depending on its 
   * 'serializeNull' settings: but, if it does not, we will end up finding that
   * whereas excluded objects are set to null, excluede properties are simply
   * missing. This is a hole in semantics I dislike very much.
   */
  /*
  public void excludeObjects( Object firstObject, Object... objects ) {
     assert objects != null;
     
     this.exclusions.put( firstObject, new HashSet<String>() );
     for( Object obj : objects ) {
        this.exclusions.put(obj, new HashSet<String>()); 
     }
  }

  package-visible boolean isObjectExcluded(Object value) {
    Set<String> objectExclusions = this.exclusions.get(value);
    if( objectExclusions == null ) {
      return false;
    }
    return objectExclusions.isEmpty();
  }
  */
}
