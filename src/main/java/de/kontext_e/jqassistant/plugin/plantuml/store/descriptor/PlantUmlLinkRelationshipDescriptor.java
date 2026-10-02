package de.kontext_e.jqassistant.plugin.plantuml.store.descriptor;


import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.xo.neo4j.api.annotation.Relation;

@Relation("LINK_TO")
public interface PlantUmlLinkRelationshipDescriptor extends Descriptor {

    boolean isHidden();
    void setHidden(boolean hidden);

    String getLabel();
    void setLabel(String label);

    String getType();
    void setType(String type);

    @Relation.Outgoing
    PlantUmlElement getOutgoing();

    @Relation.Incoming
    PlantUmlElement getIncoming();
}
