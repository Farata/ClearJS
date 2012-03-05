<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xalan="http://xml.apache.org/xslt" exclude-result-prefixes="xalan"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper">
	
	<xsl:output omit-xml-declaration="yes" method="text"/>	
	
	<xsl:template match="/|/|/" name="service-impl-jpql.xsl">
		<xsl:param name="interfaceName"/>
		<xsl:param name="methodNode"/>
		<xsl:param name="springEnabled"/>
	@DirectMethod	
	public <xsl:value-of select="$methodNode/@to-string"/> {
		<xsl:variable name="jpqlMethodNode" select="$methodNode/annotations/annotation[@name='clear.cdb.js.annotations.CX_JSJPQLMethod']"/>
		<xsl:variable name="updateInfo" select="$methodNode/annotations/annotation[@name='clear.cdb.js.annotations.CX_UpdateInfo'] | $jpqlMethodNode/method[@name='updateInfo']/value/annotation"/>
		<xsl:variable name="updateEntity" select="helper:replaceAll($updateInfo/method[@name='updateEntity']/@value, 'class ', '')"/>
		<xsl:variable name="hqlQuery" select="$jpqlMethodNode/method[@name='query']/@value"/>
		<xsl:variable name="query" select="$hqlQuery"/>
		<xsl:variable name="transferType" select="helper:getMethodTransferType($interfaceName, $methodNode/@name)"/>
		try {
			UserTransactionManager.joinUserTransaction();                        
			Session session = SessionFactoryUtils.getCurrentSession();
			Query query = session.createQuery("<xsl:value-of select="$query"/>");
			<xsl:variable name="types" select="$jpqlMethodNode/method[@name='query']/query"/>
			<xsl:for-each select="$types/parameters/parameter">
			query.setParameter("<xsl:value-of select="@name"/>", <xsl:value-of select="@name"/>);
			</xsl:for-each>
			<xsl:if test="helper:isEmptyString($transferType)">
			query.setResultTransformer(new ResultTransformer() {
				private static final long serialVersionUID = 1L;

				@Override
				public Object transformTuple(Object[] aobj, String[] as) {
					HashMap&lt;String, Object&gt; dto = new HashMap&lt;String, Object&gt;();
					<xsl:for-each select="$types/types/type">
					dto.put("<xsl:value-of select="@alias"/>", aobj[<xsl:value-of select="position()-1"/>]);
					</xsl:for-each>
					return dto;
				}
				
				@Override
				@SuppressWarnings({ "unchecked" })
				public List transformList(List list) {
					return list;
				}
			});
			</xsl:if>
			<xsl:if test="not(helper:isEmptyString($transferType))">
			query.setResultTransformer(new ResultTransformer() {
				
				private static final long serialVersionUID = 1L;
				<xsl:for-each select="$types/types/type">
					<xsl:variable name="mappedDTO" select="helper:entityToGenDTO(@name)"/>
					<xsl:variable name="mappedEntity" select="helper:getTypeAnnotation(@name, 'javax.persistence.Entity')"/>
					<xsl:choose>
						<xsl:when test="$mappedDTO!=@name">
							<xsl:variable name="entityName" select="@name"/>
							<xsl:variable name="mappedDTOorEntity">
								<xsl:variable name="isEntity" select="helper:getTypeAnnotation($transferType, 'javax.persistence.Entity')/exists"/>
								<xsl:choose>
									<xsl:when test="$isEntity">
										<xsl:value-of select="$entityName"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="$mappedDTO"/>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:variable>
				HashMap &lt;String, <xsl:value-of select="$mappedDTOorEntity"/>&gt; refDto<xsl:value-of select="position()-1"/>Map = new HashMap &lt;String, <xsl:value-of select="$mappedDTOorEntity"/>&gt;();
						</xsl:when>
					</xsl:choose>
				</xsl:for-each>
				@Override
				public Object transformTuple(Object[] aobj, String[] as) {
					<xsl:value-of select="$transferType"/> dto = new <xsl:value-of select="$transferType"/>();
					<xsl:for-each select="$types/types/type">
						<xsl:variable name="mappedDTO" select="helper:entityToGenDTO(@name)"/>
						<xsl:variable name="mappedEntity" select="helper:getTypeAnnotation(@name, 'javax.persistence.Entity')"/>
						<xsl:choose>
							<!-- xsl:when test="$mappedDTO=@name and not($mappedEntity/exists)" -->
							<xsl:when test="$mappedDTO=@name">
					dto.set<xsl:value-of select="helper:capitalizeString(@alias)"/>((<xsl:value-of select="@name"/>)aobj[<xsl:value-of select="position()-1"/>]);
							</xsl:when>
							<xsl:otherwise>
					//Create mapped DTO instance
					<xsl:value-of select="@name"/> entity<xsl:value-of select="position()-1"/> = (<xsl:value-of select="@name"/>)aobj[<xsl:value-of select="position()-1"/>];
					<xsl:variable name="mappedDTOprops" select="helper:getBeanProperties($mappedDTO)"/>
					<xsl:variable name="mappedEntityProps" select="helper:getBeanProperties(@name)"/>
					<xsl:variable name="index" select="position()-1"/>
					<xsl:variable name="entityName" select="@name"/>
					if (entity<xsl:value-of select="position()-1"/> != null) {
						String key<xsl:value-of select="$index"/> = "";
					<xsl:for-each select="$mappedDTOprops/property">
						<xsl:variable name="dtoPropName" select="@name"/>
						<xsl:if test="$mappedEntityProps/property[@name = $dtoPropName]">
							<xsl:variable name="idProp" select="helper:getBeanPropertyAnnotation($entityName, $dtoPropName, 'javax.persistence.Id')"/>
							<xsl:if test="$idProp/exists">
						key<xsl:value-of select="$index"/> += entity<xsl:value-of select="$index"/>.<xsl:value-of select="@readMethod"/>() + "|";
							</xsl:if>
							<xsl:variable name="embIdProp" select="helper:getBeanPropertyAnnotation($entityName, $dtoPropName, 'javax.persistence.EmbeddedId')"/>
							<xsl:if test="$embIdProp/exists">
						key<xsl:value-of select="$index"/> += entity<xsl:value-of select="$index"/>.<xsl:value-of select="@readMethod"/>() + "|";
							</xsl:if>
						</xsl:if>
					</xsl:for-each>
						<xsl:variable name="mappedDTOorEntity">
							<xsl:variable name="isEntity" select="helper:getTypeAnnotation($transferType, 'javax.persistence.Entity')/exists"/>
							<xsl:choose>
								<xsl:when test="$isEntity">
									<xsl:value-of select="$entityName"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="$mappedDTO"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:variable>
						//Get refDto
						<xsl:value-of select="$mappedDTOorEntity"/> refDto<xsl:value-of select="$index"/> = refDto<xsl:value-of select="$index"/>Map.get(key<xsl:value-of select="$index"/>);
						if (refDto<xsl:value-of select="$index"/> == null) {
							refDto<xsl:value-of select="$index"/> = new <xsl:value-of select="$mappedDTOorEntity"/>();
							refDto<xsl:value-of select="$index"/>Map.put(key<xsl:value-of select="$index"/>, refDto<xsl:value-of select="$index"/>);
						}
					<xsl:for-each select="$mappedDTOprops/property">
						<xsl:variable name="dtoPropName" select="@name"/>
						<xsl:if test="$mappedEntityProps/property[@name = $dtoPropName]">
							<xsl:variable name="oneToMany" select="helper:getBeanPropertyAnnotation($entityName, $dtoPropName, 'javax.persistence.OneToMany')"/>
							<xsl:variable name="entTypeName" select="helper:getBeanPropertyType($entityName, $dtoPropName)"/>
							<xsl:variable name="dtTypeName" select="helper:getBeanPropertyType($mappedDTO, $dtoPropName)"/>
							<xsl:if test="not($oneToMany/method) and $entTypeName=$dtTypeName">
						refDto<xsl:value-of select="$index"/>.<xsl:value-of select="@writeMethod"/>(entity<xsl:value-of select="$index"/>.<xsl:value-of select="@readMethod"/>());
							</xsl:if>
						</xsl:if>
					</xsl:for-each>					
						dto.set<xsl:value-of select="helper:capitalizeString(@alias)"/>(refDto<xsl:value-of select="position()-1"/>);
					}	
							</xsl:otherwise>
						</xsl:choose>
					</xsl:for-each>	
					return dto;
				}
				
				@Override
				@SuppressWarnings({ "unchecked" })
				public List transformList(List list) {
					return list;
				}
			});
			</xsl:if>
			query = <xsl:value-of select="$methodNode/@name"/>_preExecQuery(query<xsl:if test="count($methodNode/parameters/parameter)>0">, <xsl:for-each select="$methodNode/parameters/parameter"><xsl:value-of select="@name"/><xsl:if test="not(last() = position())">, </xsl:if></xsl:for-each></xsl:if>);
			@SuppressWarnings({ "unchecked" })
			List result = query.list();
			UserTransactionManager.commitUserTransaction();
			return result;
		} catch (Throwable e) {
			try {
				UserTransactionManager.rollbackUserTransaction();
			} catch (Throwable th) {
				throw new RuntimeException(th);
			}
			throw new RuntimeException(e);
		}
	}
	
	public Query <xsl:value-of select="$methodNode/@name"/>_preExecQuery(Query query<xsl:if test="count($methodNode/parameters/parameter)>0">, <xsl:for-each select="$methodNode/parameters/parameter"><xsl:value-of select="concat(@type,' ')"/><xsl:value-of select="@name"/><xsl:if test="not(last() = position())">, </xsl:if></xsl:for-each></xsl:if>) {
		Message message = ThreadLocals.getMessage();
		if (message != null) {
			Object start = message.getHeader("start");
			Object length = message.getHeader("length");
			if (start != null) {
				query.setFirstResult((Integer) start);
			}
			if (length != null) {
				query.setMaxResults((Integer) length);
			}
		}
		return query;
	}
		<xsl:if test="$updateEntity">
		<xsl:variable name="updateEntityIdPropNames">
			<xsl:variable name="keyPropsValue" select="$updateInfo/method[@name='keyPropertyNames']/@value"/>
			<xsl:choose>
				<xsl:when test="(not($keyPropsValue) or $keyPropsValue='') and $updateEntity">
					<xsl:variable name="updateEntityProps" select="helper:getBeanProperties($updateEntity)"/>				
					<xsl:for-each select="$updateEntityProps/property">
						<xsl:variable name="updateEntityId" select="helper:getBeanPropertyAnnotation($updateEntity, @name, 'javax.persistence.Id')"/>
						<xsl:variable name="updateEntityEmbId" select="helper:getBeanPropertyAnnotation($updateEntity, @name, 'javax.persistence.EmbeddedId')"/>
						<xsl:if test="$updateEntityId/exists or $updateEntityEmbId/exists">
							<xsl:value-of select="concat(@name, ',')"/>
						</xsl:if>
					</xsl:for-each>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$keyPropsValue"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="keyPropertyNames" select="helper:split($updateEntityIdPropNames, ',')"/>		
		<xsl:variable name="updatableProperties" select="$updateInfo/method[@name='updatablePropertyNames']/@value"/>
		<xsl:variable name="autoSyncEnabled" select="boolean($updateInfo/method[@name='autoSyncEnabled']/@value = 'true')"/>

		<xsl:variable name="_updatablePropertyNames">
			<xsl:choose>
				<xsl:when test="(not($updatableProperties) or $updatableProperties = '') and $updateEntity">
					<xsl:variable name="entityProps" select="helper:getBeanProperties($updateEntity)"/>
					<xsl:for-each select="$types/types/type">
						<xsl:variable name="_alias" select="@alias"/>
						<xsl:choose>
							<xsl:when test="$entityProps/property[@name=$_alias]"><xsl:value-of select="$_alias"/>,</xsl:when>
						</xsl:choose>
					</xsl:for-each>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$updatableProperties"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="updatablePropertyNames" select="helper:split($_updatablePropertyNames, ',')"/>

 	public List&lt;ChangeObject&gt; <xsl:value-of select="$methodNode/@name"/>_sync(List&lt;ChangeObject&gt; items) throws Throwable {
		try {
			UserTransactionManager.joinUserTransaction();
	 		<xsl:value-of select="$methodNode/@name"/>_deleteItems(items);
	 		<xsl:value-of select="$methodNode/@name"/>_updateItems(items);
	 		<xsl:value-of select="$methodNode/@name"/>_insertItems(items);
	 		UserTransactionManager.commitUserTransaction();
		} catch (Throwable e) {
			try {
				UserTransactionManager.rollbackUserTransaction();
			} catch (Throwable th) {
				throw new RuntimeException(th);
			}
			throw new RuntimeException(e);
		}
			
 		return items;
 	}

	@DirectMethod
	public List&lt;ChangeObject&gt; <xsl:value-of select="$methodNode/@name"/>_deleteItems(List&lt;ChangeObject&gt; items) throws Exception {
 		List&lt;ChangeObject&gt; list = null;
 		Session session = SessionFactoryUtils.getCurrentSession();
		Iterator&lt;ChangeObject&gt; iterator = items.iterator();
		list = new ArrayList&lt;ChangeObject&gt;();
		while (iterator.hasNext()) {
			clear.data.ChangeObject co = (clear.data.ChangeObject)deserializeObject((Map&lt;String, String&gt;)iterator.next(),clear.data.ChangeObject.class);
			
			
			if (co.isDelete()) {
				
				<xsl:value-of select="$transferType"/> item =  (<xsl:value-of select="$transferType"/>)deserializeObject((Map&lt;String, String&gt;)co.getPreviousVersion(), <xsl:value-of select="$transferType"/>.class);
				
				if (item!=null) {
					try {
						<xsl:choose>
						<xsl:when test="count($keyPropertyNames/element)=1">
							<xsl:for-each select="$keyPropertyNames/element">
						Object toDelete = session.get(<xsl:value-of select="$updateEntity"/>.class, item.get<xsl:value-of select="helper:capitalizeString(@value)"/>());
						session.delete(toDelete);
							</xsl:for-each>						
						</xsl:when>
						<xsl:when test="count($keyPropertyNames/element)>1">
						Query q = session.createQuery(
							"from <xsl:value-of select="$updateEntity"/> " +
							"where <xsl:for-each select="$keyPropertyNames/element"><xsl:value-of select="@value"/>=:<xsl:value-of select="@value"/><xsl:if test="not(last() = position())">, </xsl:if></xsl:for-each>");
						<xsl:for-each select="$keyPropertyNames/element">
						q.setParameter("<xsl:value-of select="@value"/>", item.get<xsl:value-of select="helper:capitalizeString(@value)"/>());
						</xsl:for-each>
						@SuppressWarnings("unchecked")
						List&lt;Object&gt; toDeleteList = q.list();
						for (Object toDelete: toDeleteList) {
							session.delete(toDelete);
						}
						</xsl:when>
						</xsl:choose>
					} catch (Exception e) {
						DataMessage cause = new DataMessage();
	                	Map&lt;String, String&gt; identity = new HashMap&lt;String, String&gt;();
						<xsl:for-each select="$keyPropertyNames/element">
						identity.put("<xsl:value-of select="@value"/>", item.get<xsl:value-of select="helper:capitalizeString(@value)"/>() + "");
						</xsl:for-each>
	                	cause.setIdentity(identity);
	                    cause.setOperation(DataMessage.DELETE_OPERATION);                       	
	                    cause.setBody(new Object[]{null,item, null});
	                    cause.setDestination("<xsl:value-of select="$interfaceName"/>");
	                    cause.setHeader("method", "<xsl:value-of select="$methodNode/@name"/>");

	                	DataSyncException dse =  new DataSyncException(co);
	                	dse.setConflictCause(cause); 
	                    throw dse;
					}
				}
				list.add(co);
			}
		}
		return list;
	} 	
	
	@DirectMethod
	public List&lt;ChangeObject&gt; <xsl:value-of select="$methodNode/@name"/>_updateItems(List&lt;ChangeObject&gt; items) throws Exception {
 		List&lt;ChangeObject&gt; list = null;
 		Session session = SessionFactoryUtils.getCurrentSession();
		
		Iterator&lt;ChangeObject&gt; iterator = items.iterator();
		list = new ArrayList&lt;ChangeObject&gt;();
		while (iterator.hasNext()) {
			clear.data.ChangeObject co = (clear.data.ChangeObject)deserializeObject((Map&lt;String, String&gt;)iterator.next(),clear.data.ChangeObject.class);
			if (co.isUpdate()) {
				
				<xsl:value-of select="$transferType"/> item =  (<xsl:value-of select="$transferType"/>)deserializeObject((Map&lt;String, String&gt;)co.getNewVersion(), <xsl:value-of select="$transferType"/>.class);
				
				if (item != null) {
					<xsl:variable name="beanProperties" select="helper:getBeanProperties($transferType)"/>
					<xsl:choose>
						<xsl:when test="count($keyPropertyNames/element)=1">
							<xsl:for-each select="$keyPropertyNames/element">
					<xsl:value-of select="$updateEntity"/> entity = new <xsl:value-of select="$updateEntity"/>();
					entity.set<xsl:value-of select="helper:capitalizeString(@value)"/>(item.get<xsl:value-of select="helper:capitalizeString(@value)"/>());
							</xsl:for-each>						
						</xsl:when>
						<xsl:when test="count($keyPropertyNames/element)>1">
					Query q = session.createQuery(
						"from <xsl:value-of select="$updateEntity"/> " +
						"where <xsl:for-each select="$keyPropertyNames/element"><xsl:value-of select="@value"/>=:<xsl:value-of select="@value"/><xsl:if test="not(last() = position())">, </xsl:if></xsl:for-each>");
						<xsl:for-each select="$keyPropertyNames/element">
					q.setParameter("<xsl:value-of select="@value"/>", item.get<xsl:value-of select="helper:capitalizeString(@value)"/>());
						</xsl:for-each>
					@SuppressWarnings("unchecked")
					List&lt;Object&gt; entities = q.list();
					<xsl:value-of select="$updateEntity"/> entity = null;
					if (entities.size()>0) {
						entity = (<xsl:value-of select="$updateEntity"/>)entities.get(0);
					}					
						</xsl:when>
						</xsl:choose>
					if (entity != null) {					
					<xsl:for-each select="$updatablePropertyNames/element">
						<xsl:variable name="props" select="helper:getBeanProperties($transferType)"/>
						<xsl:variable name="propName" select="@value"/>
						<xsl:variable name="propType" select="$props/property[@name=$propName]/@type"/>
						<xsl:choose>
							<xsl:when test="helper:genDTOtoEntity($propType)">
								<xsl:variable name="methods" select="helper:getMethods($propType)"/>
									<xsl:choose>
										<xsl:when test="$methods/method[@name='toEntity']">
						//To entity
						<xsl:value-of select="$propType"/> prop<xsl:value-of select="position()"/> = item.get<xsl:value-of select="helper:capitalizeString(@value)"/>();
						if (prop<xsl:value-of select="position()"/> != null) {
							entity.set<xsl:value-of select="helper:capitalizeString(@value)"/>(prop<xsl:value-of select="position()"/>.toEntity());
						} else {
							entity.set<xsl:value-of select="helper:capitalizeString(@value)"/>(null);
						}							
										</xsl:when>
										<xsl:otherwise>
						entity.set<xsl:value-of select="helper:capitalizeString(@value)"/>(item.get<xsl:value-of select="helper:capitalizeString(@value)"/>());
										</xsl:otherwise>
									</xsl:choose>
							</xsl:when>
							<xsl:otherwise>
						entity.set<xsl:value-of select="helper:capitalizeString(@value)"/>(item.get<xsl:value-of select="helper:capitalizeString(@value)"/>());							
							</xsl:otherwise>
						</xsl:choose>					
					</xsl:for-each>
						entity = (<xsl:value-of select="$updateEntity"/>)session.merge(entity);
						<xsl:variable name="versionProperties" select="helper:getVersionProperties($updateEntity)"/>
						<xsl:for-each select="$versionProperties/property">
						session.flush();
						<xsl:value-of select="@type"/> _<xsl:value-of select="@name"/> = entity.<xsl:value-of select="@readMethod"/>();
						item.set<xsl:value-of select="helper:capitalizeString(@name)"/>(_<xsl:value-of select="@name"/>);
						co.addChangedPropertyName("<xsl:value-of select="@name"/>");
						</xsl:for-each>
					}
				}
				list.add(co);
			}
		}
		return list;	
	} 
	
	
	
	@DirectMethod
	public List&lt;ChangeObject&gt; <xsl:value-of select="$methodNode/@name"/>_insertItems(List&lt;ChangeObject&gt; items) throws Exception {
 		List&lt;ChangeObject&gt; list = null;
 		Session session = SessionFactoryUtils.getCurrentSession();
		Iterator&lt;ChangeObject&gt; iterator = items.iterator();
		list = new ArrayList&lt;ChangeObject&gt;();
		while (iterator.hasNext()) {
			clear.data.ChangeObject co = (clear.data.ChangeObject)deserializeObject((Map&lt;String, String&gt;)iterator.next(),clear.data.ChangeObject.class);
			
			if (co.isCreate()) {
				
				<xsl:value-of select="$transferType"/> item =  (<xsl:value-of select="$transferType"/>)deserializeObject((Map&lt;String, String&gt;)co.getNewVersion(), <xsl:value-of select="$transferType"/>.class);
				
				if (item!=null) {
					<xsl:variable name="beanProperties" select="helper:getBeanProperties($transferType)"/>
					<xsl:for-each select="$beanProperties/property">
						<xsl:variable name="fxManyToOne" select="helper:getBeanPropertyAnnotation($transferType, @name, 'com.farata.dto2extjs.annotations.JSManyToOne')"/>
<xsl:message><xsl:value-of select="$transferType"/></xsl:message>						
						<xsl:variable name="propType" select="@type"/>
						<xsl:if test="$fxManyToOne/exists">
							<xsl:variable name="fxManyToOneParent" select="$propType"/>
							<xsl:variable name="fxManyToOneProperty">
								<xsl:variable name="__prop" select="$fxManyToOne/method[@name='primaryKey']/@value"/>
								<xsl:choose>
									<xsl:when test="$__prop">
										<xsl:value-of select="$__prop"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="'id'"/>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:variable>
							<xsl:if test="$fxManyToOneParent">
								<xsl:variable name="mappedEntity" select="helper:getTypeAnnotation($propType, 'javax.persistence.Entity')"/>
							<xsl:choose>
								<xsl:when test="helper:genDTOtoEntity($propType) or $mappedEntity/exists">
					if (item.<xsl:value-of select="@readMethod"/>() != null) {
						Object parentValue<xsl:value-of select="position()"/> = PropertyRack.getEntity("<xsl:value-of select="helper:replaceAll($fxManyToOneParent, '.gen._', '.')"/>", "<xsl:value-of select="$fxManyToOneProperty"/>", item.<xsl:value-of select="@readMethod"/>().get<xsl:value-of select="helper:capitalizeString($fxManyToOneProperty)"/>());
						<xsl:variable name="refProps" select="helper:getBeanProperties($fxManyToOneParent)"/>
						<xsl:variable name="refPropType" select="$refProps/property[@name=$fxManyToOneProperty]/@type"/>
						item.<xsl:value-of select="@readMethod"/>().set<xsl:value-of select="helper:capitalizeString($fxManyToOneProperty)"/>((<xsl:value-of select="$refPropType"/>) parentValue<xsl:value-of select="position()"/>);
					}
								</xsl:when>
								<xsl:otherwise>
									<xsl:variable name="foreignKey" select="$fxManyToOne/method[@name='foreignKey']/@value"/>
					Object parentValue<xsl:value-of select="position()"/> = PropertyRack.getEntity("<xsl:value-of select="helper:replaceAll($fxManyToOneParent, '.gen._', '.')"/>", "<xsl:value-of select="$fxManyToOneProperty"/>", item.get<xsl:value-of select="helper:capitalizeString($foreignKey)"/>());
					item.set<xsl:value-of select="helper:capitalizeString($foreignKey)"/>((<xsl:value-of select="helper:getBeanPropertyType(@type, $fxManyToOneProperty)"/>)parentValue<xsl:value-of select="position()"/>);									
								</xsl:otherwise>
							</xsl:choose>
							</xsl:if>
						</xsl:if>
					</xsl:for-each><xsl:text>
					</xsl:text>
					<xsl:value-of select="$updateEntity"/> entity = new <xsl:value-of select="$updateEntity"/>();
					<xsl:for-each select="$updatablePropertyNames/element">
						<xsl:variable name="props" select="helper:getBeanProperties($transferType)"/>
						<xsl:variable name="propName" select="@value"/>
						<xsl:variable name="propType" select="$props/property[@name=$propName]/@type"/>
						<xsl:choose>
							<xsl:when test="helper:genDTOtoEntity($propType)">
								<xsl:variable name="methods" select="helper:getMethods($propType)"/>
									<xsl:choose>
										<xsl:when test="$methods/method[@name='toEntity']">
					//To entity
					<xsl:value-of select="$propType"/> prop<xsl:value-of select="position()"/> = item.get<xsl:value-of select="helper:capitalizeString(@value)"/>();
					if (prop<xsl:value-of select="position()"/> != null) {
						entity.set<xsl:value-of select="helper:capitalizeString(@value)"/>(prop<xsl:value-of select="position()"/>.toEntity());
					} else {
						entity.set<xsl:value-of select="helper:capitalizeString(@value)"/>(null);
					}							
										</xsl:when>
										<xsl:otherwise>
					entity.set<xsl:value-of select="helper:capitalizeString(@value)"/>(item.get<xsl:value-of select="helper:capitalizeString(@value)"/>());
										</xsl:otherwise>
									</xsl:choose>
							</xsl:when>
							<xsl:otherwise>
					entity.set<xsl:value-of select="helper:capitalizeString(@value)"/>(item.get<xsl:value-of select="helper:capitalizeString(@value)"/>());							
							</xsl:otherwise>
						</xsl:choose>					
					</xsl:for-each>
					entity = (<xsl:value-of select="$updateEntity"/>) session.merge(entity);
					<xsl:variable name="versionProperties" select="helper:getVersionProperties($updateEntity)"/>
					<xsl:for-each select="$versionProperties/property">
					session.flush();
					<xsl:value-of select="@type"/> _<xsl:value-of select="@name"/> = entity.<xsl:value-of select="@readMethod"/>();
					item.set<xsl:value-of select="helper:capitalizeString(@name)"/>(_<xsl:value-of select="@name"/>);
					co.addChangedPropertyName("<xsl:value-of select="@name"/>");
					</xsl:for-each>
					
					<xsl:for-each select="$keyPropertyNames/element">
					Object oldValue<xsl:value-of select="position()"/> = item.get<xsl:value-of select="helper:capitalizeString(@value)"/>();
					item.set<xsl:value-of select="helper:capitalizeString(@value)"/>(entity.get<xsl:value-of select="helper:capitalizeString(@value)"/>());
					Object newValue<xsl:value-of select="position()"/> = item.get<xsl:value-of select="helper:capitalizeString(@value)"/>();
					if (oldValue<xsl:value-of select="position()"/>!=null &amp;&amp; !oldValue<xsl:value-of select="position()"/>.equals(newValue<xsl:value-of select="position()"/>))
					      co.addChangedPropertyName("<xsl:value-of select="@value"/>");
					PropertyRack.setEntity("<xsl:value-of select="$transferType"/>", "<xsl:value-of select="@value"/>", oldValue<xsl:value-of select="position()"/>, newValue<xsl:value-of select="position()"/>);
					</xsl:for-each>
					co.setNewVersion(item);
				}
				list.add(co);
			}
		}
		return list;
	} 	
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>