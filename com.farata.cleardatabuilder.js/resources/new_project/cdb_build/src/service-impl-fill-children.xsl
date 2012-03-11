<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xalan="http://xml.apache.org/xslt" exclude-result-prefixes="xalan"
	xmlns:helper="xalan://com.farata.cdb.annotations.helper.AnnotationsHelper">
	
	<xsl:output omit-xml-declaration="yes" method="text"/>	
	
	<xsl:include href="utils.xsl" />
	<xsl:template match="/|/|/" name="service-impl-fill-children.xsl">
		<xsl:param name="interfaceName"/>
		<xsl:param name="methodNode"/>
		<xsl:param name="springEnabled"/>
		
		<xsl:variable name="fillChildrenMethod" select="helper:getMethodAnnotation($interfaceName, $methodNode/@name, 'clear.cdb.js.annotations.CX_JSFillChildrenMethod')"/>
		<xsl:variable name="parentEntity" select="$fillChildrenMethod/method[@name='parent']/@value"/>
		<xsl:variable name="parentEntityProperty" select="$fillChildrenMethod/method[@name='property']/@value"/>
		<xsl:variable name="childType" select="helper:getTypeParameter(helper:getBeanPropertyType($parentEntity, $parentEntityProperty))"/>		
		<xsl:variable name="transferType" select="helper:getMethodTransferType($interfaceName, $methodNode/@name)"/>
		<xsl:variable name="mappedEntity">
			<xsl:variable name="ent" select="helper:genDTOtoEntity($transferType)"/>
			<xsl:choose>
				<xsl:when test="$ent">
					<xsl:value-of select="$ent"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$transferType"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="updateEntity" select="$mappedEntity"/>
		<xsl:variable name="parentEntityDTO">
			<xsl:choose>
				<xsl:when test="$mappedEntity != $transferType">
					<xsl:value-of select="helper:entityToGenDTO($parentEntity)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$parentEntity"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="beanProperties" select="helper:getBeanProperties($mappedEntity)"/>
		<xsl:variable name="mappedEntityIdPropNames">
			<xsl:for-each select="$beanProperties/property">
				<xsl:variable name="mappedEntityId" select="helper:getBeanPropertyAnnotation($mappedEntity, @name, 'javax.persistence.Id')"/>
				<xsl:variable name="mappedEntityEmbId" select="helper:getBeanPropertyAnnotation($mappedEntity, @name, 'javax.persistence.EmbeddedId')"/>
				<xsl:if test="$mappedEntityId/exists or $mappedEntityEmbId/exists">
					<xsl:value-of select="concat(@name, ',')"/>
				</xsl:if>
			</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="keyPropertyNames" select="helper:split($mappedEntityIdPropNames, ',')"/>
		<xsl:variable name="updatablePropertyNames" select="$beanProperties"/>
		
	public <xsl:value-of select="$methodNode/@to-string"/> {
		try {
			UserTransactionManager.joinUserTransaction();
			Session session = SessionFactoryUtils.getCurrentSession();
			<xsl:value-of select="$parentEntity"/> parentEntity = (<xsl:value-of select="$parentEntity"/>)session.get(<xsl:value-of select="$parentEntity"/>.class, <xsl:value-of select="$methodNode/parameters/parameter[1]/@name"/>);
			if (parentEntity == null) {
				return null;
			}
			<xsl:variable name="parentOneToMany" select="helper:getBeanPropertyAnnotation($parentEntity, $parentEntityProperty, 'javax.persistence.OneToMany')"/>
			<xsl:variable name="parentOneToManyProperty" select="$parentOneToMany/method[@name='mappedBy']/@value"/>
			<xsl:variable name="parentOneToManyPropertyExists" select="helper:getBeanPropertyType($transferType, $parentOneToManyProperty)"/>
			<xsl:if test="$parentOneToManyPropertyExists">
			<xsl:value-of select="$parentEntityDTO"/> parentDTO = new <xsl:value-of select="$parentEntityDTO"/>();
			<xsl:variable name="parentBeanProperties" select="helper:getBeanProperties($parentEntity)"/>
			<xsl:for-each select="$parentBeanProperties/property">
				<xsl:if test="not(@name='uid')">
					<xsl:variable name="oneToMany" select="helper:getBeanPropertyAnnotation($parentEntity, @name, 'javax.persistence.OneToMany')"/>
					<xsl:variable name="manyToOne" select="helper:getBeanPropertyAnnotation($parentEntity, @name, 'javax.persistence.ManyToOne')"/>
					<xsl:variable name="existsInDTO" select="helper:getBeanPropertyType($parentEntityDTO, @name)"/>
					<xsl:if test="not($oneToMany/exists or $manyToOne/exists) and $existsInDTO">			
			parentDTO.set<xsl:value-of select="helper:capitalizeString(@name)"/>(parentEntity.get<xsl:value-of select="helper:capitalizeString(@name)"/>());
					</xsl:if>
				</xsl:if>
			</xsl:for-each>
			</xsl:if>
			Message message = ThreadLocals.getMessage();
			Integer iStart = null;
			Integer iLength = null;
			if (message != null) {
				iStart = (Integer) message.getHeader("start");
				iLength = (Integer) message.getHeader("length");
			}
			List &lt;<xsl:value-of select="$childType"/>&gt; children = parentEntity.get<xsl:value-of select="helper:capitalizeString($parentEntityProperty)"/>();
			if (children == null) {
				return null;
			}
			if (iStart == null) {
				iStart = 0;
			}
			if (iLength == null) {
				iLength = children.size();
			}
			ArrayList &lt;<xsl:value-of select="$transferType"/>&gt; dtos = new ArrayList&lt;<xsl:value-of select="$transferType"/>&gt;();  			
			for (int i = iStart; i &lt; iLength; i++) {
				<xsl:value-of select="$childType"/> child = children.get(i);
				<xsl:value-of select="$transferType"/> dto = new <xsl:value-of select="$transferType"/>();
				<xsl:for-each select="$beanProperties/property">
					<xsl:if test="not(@name='uid' or @name=$parentOneToMany/method[@name='mappedBy']/@value)">
						<xsl:variable name="oneToMany" select="helper:getBeanPropertyAnnotation($childType, @name, 'javax.persistence.OneToMany')"/>
						<xsl:variable name="manyToOne" select="helper:getBeanPropertyAnnotation($childType, @name, 'javax.persistence.ManyToOne')"/>
						<xsl:variable name="existsInEntity" select="helper:getBeanPropertyType($childType, @name)"/>
						<xsl:if test="not($oneToMany/exists or $manyToOne/exists) and $existsInEntity">			
				dto.set<xsl:value-of select="helper:capitalizeString(@name)"/>(child.get<xsl:value-of select="helper:capitalizeString(@name)"/>());
						</xsl:if>
						<xsl:if test="$manyToOne/exists and $existsInEntity">			
							<xsl:variable name="dtoPropType" select="helper:getBeanPropertyType($transferType, @name)"/>
							<xsl:variable name="childPropType" select="helper:getBeanPropertyType($childType, @name)"/>
							<xsl:if test="$dtoPropType = $childPropType">
				dto.set<xsl:value-of select="helper:capitalizeString(@name)"/>(child.get<xsl:value-of select="helper:capitalizeString(@name)"/>());
							</xsl:if>
							
							<xsl:if test="$dtoPropType != $childPropType">
								<xsl:variable name="dtoName" select="@name"/>
				//Get <xsl:value-of select="$dtoName"/> entity
				<xsl:value-of select="$childPropType"/> _<xsl:value-of select="$dtoName"/>Entity = child.get<xsl:value-of select="helper:capitalizeString($dtoName)"/>();
				if (_<xsl:value-of select="$dtoName"/>Entity != null) {
					//Create <xsl:value-of select="$dtoName"/> DTO
					<xsl:value-of select="$dtoPropType"/> _<xsl:value-of select="$dtoName"/>Dto = new <xsl:value-of select="$dtoPropType"/>();				
								<xsl:variable name="dtoBeanProperties" select="helper:getBeanProperties($dtoPropType)"/>
								<xsl:for-each select="$dtoBeanProperties/property">
									<xsl:if test="not(@name='uid')">
										<xsl:variable name="dtoOneToMany" select="helper:getBeanPropertyAnnotation($childPropType, @name, 'javax.persistence.OneToMany')"/>
										<xsl:variable name="dtoManyToOne" select="helper:getBeanPropertyAnnotation($childPropType, @name, 'javax.persistence.ManyToOne')"/>
										<xsl:if test="not($dtoOneToMany/exists or $dtoManyToOne/exists)">
					_<xsl:value-of select="$dtoName"/>Dto.set<xsl:value-of select="helper:capitalizeString(@name)"/>(_<xsl:value-of select="$dtoName"/>Entity.get<xsl:value-of select="helper:capitalizeString(@name)"/>());
										</xsl:if>
									</xsl:if>
								</xsl:for-each>
					dto.set<xsl:value-of select="helper:capitalizeString($dtoName)"/>(_<xsl:value-of select="$dtoName"/>Dto);
				}				
							</xsl:if>
						</xsl:if>
					</xsl:if>
				</xsl:for-each>
				<xsl:if test="$parentOneToManyPropertyExists">
				dto.set<xsl:value-of select="helper:capitalizeString($parentOneToManyProperty)"/>(parentDTO);
				</xsl:if>
				dtos.add(dto);
			}
			UserTransactionManager.commitUserTransaction();
			return dtos;
		} catch (Throwable e) {
			try {
				UserTransactionManager.rollbackUserTransaction();
			} catch (Throwable th) {
				throw new RuntimeException(th);
			}
			throw new RuntimeException(e);
		}
	}
	
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
 	
	public List&lt;ChangeObject&gt; <xsl:value-of select="$methodNode/@name"/>_deleteItems(List&lt;ChangeObject&gt; items) throws Exception {
 		List&lt;ChangeObject&gt; list = null;
 		Session session = SessionFactoryUtils.getCurrentSession();
		ChangeObject co = null;
		Iterator&lt;ChangeObject&gt; iterator = items.iterator();
		list = new ArrayList&lt;ChangeObject&gt;();
		while (iterator.hasNext()) {
			co = (ChangeObject) iterator.next();
			if (co.isDelete()) {
				<xsl:value-of select="$transferType"/> item = (<xsl:value-of select="$transferType"/>) co.getPreviousVersion();
				if (item!=null) {
					try {
						<xsl:choose>
						<xsl:when test="count($keyPropertyNames/element)=1">
							<xsl:for-each select="$keyPropertyNames/element">
						Object toDelete = session.get(<xsl:value-of select="$updateEntity"/>.class, item.get<xsl:value-of select="helper:capitalizeString(@value)"/>());
						<xsl:value-of select="$methodNode/@name"/>_beforeDelete(session, toDelete);
						session.delete(toDelete);
						<xsl:value-of select="$methodNode/@name"/>_afterDelete(session, toDelete);
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
							<xsl:value-of select="$methodNode/@name"/>_beforeDelete(session, toDelete);
							session.delete(toDelete);
							<xsl:value-of select="$methodNode/@name"/>_afterDelete(session, toDelete);
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

	public List&lt;ChangeObject&gt; <xsl:value-of select="$methodNode/@name"/>_updateItems(List&lt;ChangeObject&gt; items) throws Exception {
 		List&lt;ChangeObject&gt; list = null;
 		Session session = SessionFactoryUtils.getCurrentSession();
		ChangeObject co = null;
		Iterator&lt;ChangeObject&gt; iterator = items.iterator();
		list = new ArrayList&lt;ChangeObject&gt;();
		while (iterator.hasNext()) {
			co = (ChangeObject) iterator.next();
			if (co.isUpdate()) {
				<xsl:value-of select="$transferType"/> item = (<xsl:value-of select="$transferType"/>) co.getNewVersion();
				if (item != null) {
					<xsl:variable name="beanProperties1" select="helper:getBeanProperties($transferType)"/>
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
						<xsl:call-template name="copyDto">
							<xsl:with-param name="fromName" select="'item'"/>
							<xsl:with-param name="fromType" select="$transferType"/>
							<xsl:with-param name="toName" select="'entity'"/>
							<xsl:with-param name="toType" select="$updateEntity"/>
							<xsl:with-param name="indent" select="'&#09;&#09;&#09;&#09;&#09;&#09;'"/>
						</xsl:call-template>
						//Store entity
						<xsl:value-of select="$methodNode/@name"/>_beforeUpdate(session, entity);
						entity = (<xsl:value-of select="$updateEntity"/>)session.merge(entity);
						<xsl:value-of select="$methodNode/@name"/>_afterUpdate(session, entity);
						<xsl:variable name="versionProperties" select="helper:getVersionProperties($updateEntity)"/>
						<xsl:for-each select="$versionProperties/property">
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
	
	public List&lt;ChangeObject&gt; <xsl:value-of select="$methodNode/@name"/>_insertItems(List&lt;ChangeObject&gt; items) throws Exception {
 		List&lt;ChangeObject&gt; list = null;
 		Session session = SessionFactoryUtils.getCurrentSession();
		ChangeObject co = null;
		Iterator&lt;ChangeObject&gt; iterator = items.iterator();
		list = new ArrayList&lt;ChangeObject&gt;();
		while (iterator.hasNext()) {
			co = (ChangeObject) iterator.next();
			if (co.isCreate()) {
				<xsl:value-of select="$transferType"/> item = (<xsl:value-of select="$transferType"/>) co.getNewVersion();
				if (item!=null) {
					<xsl:variable name="beanProperties" select="helper:getBeanProperties($transferType)"/>
					<xsl:for-each select="$beanProperties/property">
						<xsl:variable name="fxManyToOne" select="helper:getBeanPropertyAnnotation($transferType, @name, 'com.farata.dto2extjs.annotations.JSManyToOne')"/>
						<xsl:variable name="propType" select="@type"/>
						<xsl:if test="$fxManyToOne">
							<xsl:variable name="fxManyToOneParent" select="$fxManyToOne/method[@name='parent']/@value"/>
							<xsl:variable name="fxManyToOneProperty" select="$fxManyToOne/method[@name='property']/@value"/>
							<xsl:if test="$fxManyToOneParent">
								<xsl:variable name="mappedEntity" select="helper:getTypeAnnotation($propType, 'javax.persistence.Entity')"/>
							<xsl:choose>
								<xsl:when test="helper:genDTOtoEntity($propType) or $mappedEntity/exists">
					if (item.<xsl:value-of select="@readMethod"/>() != null) {
						Object parentValue<xsl:value-of select="position()"/> = PropertyRack.getEntity("<xsl:value-of select="helper:replaceAll($fxManyToOneParent, '.$', '.')"/>", "<xsl:value-of select="$fxManyToOneProperty"/>", item.<xsl:value-of select="@readMethod"/>().get<xsl:value-of select="helper:capitalizeString($fxManyToOneProperty)"/>());
						<xsl:value-of select="$fxManyToOneParent"/> parentDTO<xsl:value-of select="position()"/> = new <xsl:value-of select="$fxManyToOneParent"/>();
						<xsl:variable name="refProps" select="helper:getBeanProperties($fxManyToOneParent)"/>
						<xsl:variable name="refPropType" select="$refProps/property[@name=$fxManyToOneProperty]/@type"/>
						parentDTO<xsl:value-of select="position()"/>.set<xsl:value-of select="helper:capitalizeString($fxManyToOneProperty)"/>((<xsl:value-of select="$refPropType"/>) parentValue<xsl:value-of select="position()"/>);
						
						item.<xsl:value-of select="@writeMethod"/>(parentDTO<xsl:value-of select="position()"/>);
					}
								</xsl:when>
								<xsl:otherwise>
					Object parentValue<xsl:value-of select="position()"/> = PropertyRack.getEntity("<xsl:value-of select="helper:replaceAll($fxManyToOneParent, '.$', '.')"/>", "<xsl:value-of select="$fxManyToOneProperty"/>", item.<xsl:value-of select="@readMethod"/>());
					item.<xsl:value-of select="@writeMethod"/>((<xsl:value-of select="@type"/>)parentValue<xsl:value-of select="position()"/>);									
								</xsl:otherwise>
							</xsl:choose>
							</xsl:if>
						</xsl:if>
					</xsl:for-each><xsl:text>
					</xsl:text>
					<xsl:value-of select="$updateEntity"/> entity = new <xsl:value-of select="$updateEntity"/>();
					<xsl:call-template name="copyDto">
						<xsl:with-param name="fromName" select="'item'"/>
						<xsl:with-param name="fromType" select="$transferType"/>
						<xsl:with-param name="toName" select="'entity'"/>
						<xsl:with-param name="toType" select="$updateEntity"/>
						<xsl:with-param name="indent" select="'&#09;&#09;&#09;&#09;&#09;'"/>
					</xsl:call-template>
					//Store entity
					<xsl:value-of select="$methodNode/@name"/>_beforeInsert(session, entity);
					entity = (<xsl:value-of select="$updateEntity"/>)session.merge(entity);
					<xsl:value-of select="$methodNode/@name"/>_afterInsert(session, entity);
					<xsl:variable name="versionProperties" select="helper:getVersionProperties($updateEntity)"/>
					<xsl:for-each select="$versionProperties/property">
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
	
	public void <xsl:value-of select="$methodNode/@name"/>_beforeDelete(Session session, Object entity) {
	}

	public void <xsl:value-of select="$methodNode/@name"/>_afterDelete(Session session, Object entity) {
	}

	public void <xsl:value-of select="$methodNode/@name"/>_beforeInsert(Session session, Object entity) {
	}

	public void <xsl:value-of select="$methodNode/@name"/>_afterInsert(Session session, Object entity) {
	}

	public void <xsl:value-of select="$methodNode/@name"/>_beforeUpdate(Session session, Object entity) {
	}

	public void <xsl:value-of select="$methodNode/@name"/>_afterUpdate(Session session, Object entity) {
	}
	</xsl:template>
	<xsl:template name="copy"></xsl:template>
</xsl:stylesheet>