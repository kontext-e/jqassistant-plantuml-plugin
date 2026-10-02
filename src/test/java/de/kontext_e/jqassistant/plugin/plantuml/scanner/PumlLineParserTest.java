package de.kontext_e.jqassistant.plugin.plantuml.scanner;

import com.buschmais.jqassistant.core.store.api.Store;
import de.kontext_e.jqassistant.plugin.plantuml.store.descriptor.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.smallrye.common.constraint.Assert.assertTrue;
import static java.util.Arrays.stream;
import static org.assertj.core.api.Assertions.allOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class PumlLineParserTest {
    private PlantUMLLineParser plantUMLLineParser;
    private final Store mockStore = mock(Store.class);
    private final PlantUmlFileDescriptor plantUmlFileDescriptor = mock(PlantUmlFileDescriptor.class);

    @BeforeEach
    public void setUp() {
        plantUMLLineParser = new PlantUMLLineParser(mockStore, plantUmlFileDescriptor, ParsingState.ACCEPTING);
    }

    @Test
    public void thatSimplePackagePumlFileIsRead() {
        final String puml = "@startuml\n" +
                            "\n" +
                            "package de.kontext_e.packages.a {}\n" +
                            "package de.kontext_e.packages.b {}\n" +
                            "\n" +
                            "de.kontext_e.packages.a <-up[hidden]- de.kontext_e.packages.b : some label\n" +
                            "\n" +
                            "@enduml\n";

        String[] lines = puml.split("\\n");
        plantUMLLineParser = new PlantUMLLineParser(mockStore, plantUmlFileDescriptor, ParsingState.ACCEPTING);

        final PlantUmlClassDiagramDescriptor mockPlantUmlClassDiagramDescriptor = mock(PlantUmlClassDiagramDescriptor.class);
        when(mockStore.create(PlantUmlClassDiagramDescriptor.class)).thenReturn(mockPlantUmlClassDiagramDescriptor);
        final PlantUmlPackageDescriptor mockPackageDescriptor = mock(PlantUmlPackageDescriptor.class);
        when(mockStore.create(PlantUmlPackageDescriptor.class)).thenReturn(mockPackageDescriptor);
        final PlantUmlLinkRelationshipDescriptor mockRelationshipDescriptor = mock(PlantUmlLinkRelationshipDescriptor.class);
        when(mockStore.create(any(PlantUmlElement.class), eq(PlantUmlLinkRelationshipDescriptor.class), any(PlantUmlElement.class))).thenReturn(mockRelationshipDescriptor);


        stream(lines).forEach(line -> plantUMLLineParser.parseLine(line));

        verify(mockStore).create(PlantUmlClassDiagramDescriptor.class);
        verify(mockStore, times(2)).create(PlantUmlPackageDescriptor.class);
        verify(mockPlantUmlClassDiagramDescriptor).setType("CLASSDIAGRAM");
        verify(mockPackageDescriptor).setFullName("de.kontext_e.packages.a");
        verify(mockPackageDescriptor).setFullName("de.kontext_e.packages.b");
        verify(mockRelationshipDescriptor).setHidden(true);
        verify(mockRelationshipDescriptor).setLabel("[some label]");
        verify(mockRelationshipDescriptor).setType("NORMAL");
    }

    @Test
    public void thatSimplePackagePumlFileIsReadWithDottedLines() {
        final String puml = "@startuml\n" +
                            "\n" +
                            "package de.kontext_e.packages.a {}\n" +
                            "package de.kontext_e.packages.b {}\n" +
                            "\n" +
                            "de.kontext_e.packages.a ..> de.kontext_e.packages.b\n" +
                            "\n" +
                            "@enduml\n";

        String[] lines = puml.split("\\n");
        plantUMLLineParser = new PlantUMLLineParser(mockStore, plantUmlFileDescriptor, ParsingState.ACCEPTING);

        final PlantUmlClassDiagramDescriptor mockPlantUmlClassDiagramDescriptor = mock(PlantUmlClassDiagramDescriptor.class);
        when(mockStore.create(PlantUmlClassDiagramDescriptor.class)).thenReturn(mockPlantUmlClassDiagramDescriptor);
        final PlantUmlPackageDescriptor mockPackageDescriptor = mock(PlantUmlPackageDescriptor.class);
        when(mockStore.create(PlantUmlPackageDescriptor.class)).thenReturn(mockPackageDescriptor);
        final PlantUmlLinkRelationshipDescriptor mockRelationshipDescriptor = mock(PlantUmlLinkRelationshipDescriptor.class);
        when(mockStore.create(any(PlantUmlElement.class), eq(PlantUmlLinkRelationshipDescriptor.class), any(PlantUmlElement.class))).thenReturn(mockRelationshipDescriptor);


        stream(lines).forEach(line -> plantUMLLineParser.parseLine(line));

        verify(mockStore).create(PlantUmlClassDiagramDescriptor.class);
        verify(mockStore, times(2)).create(PlantUmlPackageDescriptor.class);
        verify(mockPlantUmlClassDiagramDescriptor).setType("CLASSDIAGRAM");
        verify(mockPackageDescriptor).setFullName("de.kontext_e.packages.a");
        verify(mockPackageDescriptor).setFullName("de.kontext_e.packages.b");
        verify(mockRelationshipDescriptor).setHidden(false);
        verify(mockRelationshipDescriptor).setLabel("NULL");
        verify(mockRelationshipDescriptor).setType("DASHED");
    }

    @Test
    public void thatPackagePumlFileIsRead() {
        final String puml = "@startuml\n" +
                            "\n" +
                            "skinparam packageStyle rect\n" +
                            "\n" +
                            "class SomeClass \n" +
                            "package scanner {}\n" +
                            "package store {\n" +
                            "    package descriptor {\n" +
                            "       interface PlantUmlDescriptor\n"+
                            "    }\n" +
                            "}\n" +
                            "\n" +
                            "scanner --> descriptor\n" +
                            "SomeClass --> PlantUmlDescriptor\n" +
                            "\n" +
                            "@enduml\n";
        String[] lines = puml.split("\\n");
        plantUMLLineParser = new PlantUMLLineParser(mockStore, plantUmlFileDescriptor, ParsingState.ACCEPTING);

        final PlantUmlClassDiagramDescriptor mockPlantUmlClassDiagramDescriptor = mock(PlantUmlClassDiagramDescriptor.class);
        when(mockStore.create(PlantUmlClassDiagramDescriptor.class)).thenReturn(mockPlantUmlClassDiagramDescriptor);
        final PlantUmlPackageDescriptor mockPackageDescriptor = mock(PlantUmlPackageDescriptor.class);
        when(mockStore.create(PlantUmlPackageDescriptor.class)).thenReturn(mockPackageDescriptor);
        final PlantUmlLeafDescriptor mockPlantUmlLeafDescriptor = mock(PlantUmlLeafDescriptor.class);
        when(mockStore.create(PlantUmlLeafDescriptor.class)).thenReturn(mockPlantUmlLeafDescriptor);
        final PlantUmlLinkRelationshipDescriptor mockRelationshipDescriptor = mock(PlantUmlLinkRelationshipDescriptor.class);
        when(mockStore.create(any(PlantUmlElement.class), eq(PlantUmlLinkRelationshipDescriptor.class), any(PlantUmlElement.class))).thenReturn(mockRelationshipDescriptor);

        stream(lines).forEach(line -> plantUMLLineParser.parseLine(line));

        verify(mockStore).create(PlantUmlClassDiagramDescriptor.class);
        verify(mockStore, times(2)).create(PlantUmlLeafDescriptor.class);
        verify(mockStore, times(3)).create(PlantUmlPackageDescriptor.class);
        verify(mockPackageDescriptor, times(1)).getChildGroups();
        verify(mockPlantUmlClassDiagramDescriptor).setType("CLASSDIAGRAM");
        verify(mockPlantUmlLeafDescriptor, times(1)).setType("INTERFACE");
        verify(mockPlantUmlLeafDescriptor, times(1)).setType("CLASS");
        verify(mockRelationshipDescriptor, times(2)).setHidden(false);
        verify(mockRelationshipDescriptor, times(2)).setLabel("NULL");
        verify(mockRelationshipDescriptor, times(2)).setType("NORMAL");

        verify(mockPackageDescriptor, times(1)).setFullName("scanner");
        verify(mockPackageDescriptor, times(1)).setFullName("store");
        verify(mockPackageDescriptor, times(1)).setFullName("store.descriptor");
        verify(mockPlantUmlLeafDescriptor, times(1)).setFullName("someclass");
        verify(mockPlantUmlLeafDescriptor, times(1)).setType("CLASS");
        verify(mockPlantUmlLeafDescriptor, times(1)).setDescription("someclass\n");
        verify(mockPlantUmlLeafDescriptor, times(1)).setFullName("store.descriptor.plantumldescriptor");
        verify(mockPlantUmlLeafDescriptor, times(1)).setType("INTERFACE");
        verify(mockPlantUmlLeafDescriptor, times(1)).setDescription("plantumldescriptor\n");
    }

    @Test
    public void thatComponentPumlFileIsRead() {
        final String puml = "@startuml\n" +
                            "\n" +
                            "skinparam componentStyle uml2\n" +
                            "skinparam component {\n" +
                            "  FontSize 13\n" +
                            "  FontName Arial\n" +
                            "  FontColor #99c0d0\n" +
                            "  BorderColor black\n" +
                            "  BackgroundColor #42788e\n" +
                            "  ArrowFontName Impact\n" +
                            "  ArrowColor #42788e\n" +
                            "  ArrowFontColor #42788e\n" +
                            "\n" +
                            "\n" +
                            "  BackgroundColor<<UI>> Red\n" +
                            "  BorderColor<<UI>> #FF6655\n" +
                            "}\n" +
                            "\n" +
                            "component TestComponent1 <<UI>> <<abstract>> [\n" +
                            "     <size:20><b><u>TestComponent1</u></b></size>\n" +
                            "]\n" +
                            "component TestComponent2 [\n" +
                            "     <size:20><b><u>TestComponent2</u></b></size>\n" +
                            "]\n" +
                            "\n" +
                            "TestComponent1 --> TestComponent2\n" +
                            "\n" +
                            "@enduml\n";
        String[] lines = puml.split("\\n");
        plantUMLLineParser = new PlantUMLLineParser(mockStore, plantUmlFileDescriptor, ParsingState.ACCEPTING);

        final PlantUmlClassDiagramDescriptor mockClassDiagramDescriptor = mock(PlantUmlClassDiagramDescriptor.class);
        when(mockStore.create(PlantUmlClassDiagramDescriptor.class)).thenReturn(mockClassDiagramDescriptor);
        final PlantUmlLeafDescriptor mockPlantUmlLeafDescriptor = mock(PlantUmlLeafDescriptor.class);
        when(mockStore.create(PlantUmlLeafDescriptor.class)).thenReturn(mockPlantUmlLeafDescriptor);
        final PlantUmlLinkRelationshipDescriptor mockRelationshipDescriptor = mock(PlantUmlLinkRelationshipDescriptor.class);
        when(mockStore.create(any(PlantUmlElement.class), eq(PlantUmlLinkRelationshipDescriptor.class), any(PlantUmlElement.class))).thenReturn(mockRelationshipDescriptor);

        stream(lines).forEach(line -> plantUMLLineParser.parseLine(line));

        verify(mockStore).create(PlantUmlClassDiagramDescriptor.class);
        verify(mockStore, times(2)).create(PlantUmlLeafDescriptor.class);
		verify(mockClassDiagramDescriptor).setType("CLASSDIAGRAM");
        verify(mockPlantUmlLeafDescriptor, times(2)).setType("DESCRIPTION");
        verify(mockRelationshipDescriptor).setHidden(false);
        verify(mockRelationshipDescriptor).setLabel("NULL");
        verify(mockRelationshipDescriptor).setType("NORMAL");

        verify(mockPlantUmlLeafDescriptor).setFullName("testcomponent1");
        verify(mockPlantUmlLeafDescriptor, times(2)).setType("DESCRIPTION");
        verify(mockPlantUmlLeafDescriptor).setDescription("<size:20><b><u>testcomponent1</u></b></size>\n");

        // stereotypes can be «» or <<>>, so just check content
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(mockPlantUmlLeafDescriptor).setStereotype(argument.capture());
        assertTrue(argument.getValue().contains("ui"));
        assertTrue(argument.getValue().contains("abstract"));

        verify(mockPlantUmlLeafDescriptor).setFullName("testcomponent2");
        verify(mockPlantUmlLeafDescriptor, times(2)).setType("DESCRIPTION");
        verify(mockPlantUmlLeafDescriptor).setDescription("<size:20><b><u>testcomponent2</u></b></size>\n");

    }

    @Test
    public void thatStateDiagramIsRead() {
        final String puml = "@startuml\n" +
                               "\n" +
                               "[*] --> State1\n" +
                               "State1 --> [*]\n" +
                               "State1 : this is a string\n" +
                               "State1 : this is another string\n" +
                               "\n" +
                               "State1 -> State2\n" +
                               "State2 --> [*]\n" +
                               "\n" +
                               "@enduml";

        String[] lines = puml.split("\\n");
        plantUMLLineParser = new PlantUMLLineParser(mockStore, plantUmlFileDescriptor, ParsingState.ACCEPTING);

        final PlantUmlStateDiagramDescriptor mockDescriptor = mock(PlantUmlStateDiagramDescriptor.class);
        when(mockStore.create(PlantUmlStateDiagramDescriptor.class)).thenReturn(mockDescriptor);
        final PlantUmlLeafDescriptor mockPlantUmlLeafDescriptor = mock(PlantUmlLeafDescriptor.class);
        when(mockStore.create(PlantUmlLeafDescriptor.class)).thenReturn(mockPlantUmlLeafDescriptor);
        final PlantUmlLinkRelationshipDescriptor mockRelationshipDescriptor = mock(PlantUmlLinkRelationshipDescriptor.class);
        when(mockStore.create(any(PlantUmlElement.class), eq(PlantUmlLinkRelationshipDescriptor.class), any(PlantUmlElement.class))).thenReturn(mockRelationshipDescriptor);

        stream(lines).forEach(line -> plantUMLLineParser.parseLine(line));

        verify(mockStore).create(PlantUmlStateDiagramDescriptor.class);
        verify(mockStore, times(4)).create(PlantUmlLeafDescriptor.class);
		verify(mockDescriptor).setType("STATEDIAGRAM");
        verify(mockPlantUmlLeafDescriptor, times(1)).setType("CIRCLE_START");
        verify(mockPlantUmlLeafDescriptor, times(2)).setType("STATE");
        verify(mockPlantUmlLeafDescriptor, times(1)).setType("CIRCLE_END");
        verify(mockPlantUmlLeafDescriptor, times(1)).setFullName("*start*");
        verify(mockPlantUmlLeafDescriptor, times(1)).setFullName("state1");
        verify(mockPlantUmlLeafDescriptor, times(1)).setFullName("state2");
        verify(mockPlantUmlLeafDescriptor, times(1)).setFullName("*end*");

        verify(mockRelationshipDescriptor, times(4)).setHidden(false);
        verify(mockRelationshipDescriptor, times(4)).setLabel("NULL");
        verify(mockRelationshipDescriptor, times(4)).setType("NORMAL");
    }

    @Test
    public void thatSequenceDiagramIsRead() {
        final String puml = "@startuml\n" +
                "title First Level Components\n" +
                "caption Level 1 Building Blocks\n" +
                "\n" +
                "legend right\n" +
                "    | Element | Description |\n" +
                "    |<#42788e>| Component |\n" +
                "    | A --> B | A depends on B |\n" +
                "endlegend\n" +
                "\n" +

                "autonumber\n" +
                "participant de.kontext_e.spikes.trace_to_plantuml.application.Controller <<Controller>>\n" +
                "de.kontext_e.spikes.trace_to_plantuml.application.Controller -> de.kontext_e.spikes.trace_to_plantuml.application.Boundary : loadEntity([1])\n" +
                "de.kontext_e.spikes.trace_to_plantuml.application.Boundary -> de.kontext_e.spikes.trace_to_plantuml.application.Repository : readEntity([1])\n" +
                "de.kontext_e.spikes.trace_to_plantuml.application.Repository -> de.kontext_e.spikes.trace_to_plantuml.application.Boundary : throws(NoSuchEntityException{id=1})\n" +
                "de.kontext_e.spikes.trace_to_plantuml.application.Boundary -> de.kontext_e.spikes.trace_to_plantuml.application.Controller : return([LoadEntityResult{results=NO_RESULT}])\n" +
                "de.kontext_e.spikes.trace_to_plantuml.application.Controller -> de.kontext_e.spikes.trace_to_plantuml.application.LoadEntityResult : getResults([])\n" +
                "de.kontext_e.spikes.trace_to_plantuml.application.LoadEntityResult -> de.kontext_e.spikes.trace_to_plantuml.application.Controller : return([NO_RESULT])\n" +
                "@enduml";

        String[] lines = puml.split("\\n");
        plantUMLLineParser = new PlantUMLLineParser(mockStore, plantUmlFileDescriptor, ParsingState.ACCEPTING);

        final PlantUmlSequenceDiagramDescriptor mockDescriptor = mock(PlantUmlSequenceDiagramDescriptor.class);
        when(mockStore.create(PlantUmlSequenceDiagramDescriptor.class)).thenReturn(mockDescriptor);
        final PlantUmlParticipantDescriptor participantDescriptor = mock(PlantUmlParticipantDescriptor.class);
        when(mockStore.create(PlantUmlParticipantDescriptor.class)).thenReturn(participantDescriptor);
        final PlantUmlSequenceDiagramMessageDescriptor messageDescriptor = mock(PlantUmlSequenceDiagramMessageDescriptor.class);
        when(mockStore.create(participantDescriptor, PlantUmlSequenceDiagramMessageDescriptor.class, participantDescriptor)).thenReturn(messageDescriptor);
        final PlantUmlLinkRelationshipDescriptor mockRelationshipDescriptor = mock(PlantUmlLinkRelationshipDescriptor.class);
        when(mockStore.create(any(PlantUmlElement.class), eq(PlantUmlLinkRelationshipDescriptor.class), any(PlantUmlElement.class))).thenReturn(mockRelationshipDescriptor);

        stream(lines).forEach(line -> plantUMLLineParser.parseLine(line));

        verify(mockStore).create(PlantUmlSequenceDiagramDescriptor.class);
        verify(mockDescriptor).setType("SEQUENCEDIAGRAM");
        verify(mockDescriptor).setTitle("first level components");
        verify(mockDescriptor).setCaption("level 1 building blocks");
        verify(mockDescriptor).setLegend("| element | description ||<#42788e>| component || a --> b | a depends on b |");
        verify(participantDescriptor, times(4)).setType("PARTICIPANT");
        verify(participantDescriptor).setName("de.kontext_e.spikes.trace_to_plantuml.application.controller");

        // stereotypes can be «» or <<>>, so just check content
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(participantDescriptor).setStereotype(argument.capture());
        assertTrue(argument.getValue().contains("controller"));

        verify(messageDescriptor).setMessage("[loadentity([1])]");
        verify(messageDescriptor).setMessageNumber("<b>1</b>");
    }


	@Test
    public void thatEmbeddedPlantUMLInAsciidocIsRead() {
        final String asciidoc = "=== Level 1\n" +
                                "\n" +
                                "\n" +
                                "The following diagram shows the main building blocks of the system and their interdependencies:\n" +
                                "\n" +
                                "[\"plantuml\",\"MainBuildingBlocks.png\",\"png\"]\n" +
                                "----\n" +
                                "package de.kontext_e.project.domain #ffffff {\n" +
                                "}\n" +
                                "package de.kontext_e.project.services #ffffff {\n" +
                                "}\n" +
                                "\n" +
                                "de.kontext_e.project.services --> de.kontext_e.project.domain\n" +
                                "\n" +
                                "-----\n" +
                                "\n" +
                                "Comments regarding structure and interdependencies at Level 1:\n";
        String[] lines = asciidoc.split("\\n");
        plantUMLLineParser = new PlantUMLLineParser(mockStore, plantUmlFileDescriptor, ParsingState.IGNORING);

        final PlantUmlClassDiagramDescriptor mockPlantUmlClassDiagramDescriptor = mock(PlantUmlClassDiagramDescriptor.class);
        when(mockStore.create(PlantUmlClassDiagramDescriptor.class)).thenReturn(mockPlantUmlClassDiagramDescriptor);
        final PlantUmlPackageDescriptor mockPackageDescriptor = mock(PlantUmlPackageDescriptor.class);
        when(mockStore.create(PlantUmlPackageDescriptor.class)).thenReturn(mockPackageDescriptor);
        final PlantUmlLeafDescriptor mockPlantUmlLeafDescriptor = mock(PlantUmlLeafDescriptor.class);
        when(mockStore.create(PlantUmlLeafDescriptor.class)).thenReturn(mockPlantUmlLeafDescriptor);
        final PlantUmlLinkRelationshipDescriptor mockRelationshipDescriptor = mock(PlantUmlLinkRelationshipDescriptor.class);
        when(mockStore.create(any(PlantUmlElement.class), eq(PlantUmlLinkRelationshipDescriptor.class), any(PlantUmlElement.class))).thenReturn(mockRelationshipDescriptor);

        stream(lines).forEach(line -> plantUMLLineParser.parseLine(line));

        verify(mockStore).create(PlantUmlClassDiagramDescriptor.class);
        verify(mockStore, times(2)).create(PlantUmlPackageDescriptor.class);
        verify(mockRelationshipDescriptor).setHidden(false);
        verify(mockRelationshipDescriptor).setLabel("NULL");
        verify(mockRelationshipDescriptor).setType("NORMAL");
    }

    @Test
    public void thatEmbeddedPlantUMLInAsciidocIsRead2() {
        String[] lines = ("=== Level 1\n" +
                                        "\n" +
                                        "\n" +
                                        "The following diagram shows the main building blocks of the system and their interdependencies:\n" +
                                        "\n" +
                                        "[\"plantuml\",\"MainBuildingBlocks\",\"png\"]\n" +
                                        "-----\n" +
                                        "component Backend\n" +
                                        "component [Some UI]\n" +
                                        "\n" +
                                        "[Some UI] --> Backend\n" +
                                        "-----\n" +
                                        "\n" +
                                        "Comments regarding structure and interdependencies at Level 1:\n")
                .split("\\n");
        plantUMLLineParser = new PlantUMLLineParser(mockStore, plantUmlFileDescriptor, ParsingState.IGNORING);

        final PlantUmlDescriptionDiagramDescriptor mockDescriptionDiagramDescriptor = mock(PlantUmlDescriptionDiagramDescriptor.class);
        when(mockStore.create(PlantUmlDescriptionDiagramDescriptor.class)).thenReturn(mockDescriptionDiagramDescriptor);
        final PlantUmlLeafDescriptor mockPlantUmlLeafDescriptor = mock(PlantUmlLeafDescriptor.class);
        when(mockStore.create(PlantUmlLeafDescriptor.class)).thenReturn(mockPlantUmlLeafDescriptor);
        final Set<PlantUmlElement> leafs = new HashSet<>();
        when(mockDescriptionDiagramDescriptor.getLeafs()).thenReturn(leafs);
        final PlantUmlLinkRelationshipDescriptor mockRelationshipDescriptor = mock(PlantUmlLinkRelationshipDescriptor.class);
        when(mockStore.create(any(PlantUmlElement.class), eq(PlantUmlLinkRelationshipDescriptor.class), any(PlantUmlElement.class))).thenReturn(mockRelationshipDescriptor);

        stream(lines).forEach(line -> plantUMLLineParser.parseLine(line));


        verify(mockStore).create(PlantUmlDescriptionDiagramDescriptor.class);
        verify(mockStore, times(2)).create(PlantUmlLeafDescriptor.class);
        verify(mockPlantUmlLeafDescriptor, times(2)).setType("DESCRIPTION");
        verify(mockPlantUmlLeafDescriptor, times(1)).setFullName("backend");
        verify(mockPlantUmlLeafDescriptor, times(1)).setFullName("some ui");
        verify(mockRelationshipDescriptor).setHidden(false);
        verify(mockRelationshipDescriptor).setLabel("NULL");
        verify(mockRelationshipDescriptor).setType("NORMAL");
        // mock returns two times the same other mock
        // so only one entry is in the map
        assertThat(leafs.size()).isEqualTo(1);
    }

    @Test
    public void thatEmbeddedPlantUMLInAsciidocIsRead3() {
        String[] lines = ("The Backend is structured with Domain Components in the first level. Could look like this:\n" +
                          "\n" +
                          "[\"plantuml\",\"Backend\",\"png\"]\n" +
                          "-----\n" +
                          "!include puml.style\n" +
                          "!define FOR table_id\n" +
                          "title Backend\n" +
                          "\n" +
                          "component customer <<Business>><<com.example.application.customer>> [\n" +
                          "    componentName(Customer)\n" +
                          "\n" +
                          "    com.example.application.customer\n" +
                          "]\n" +
                          "note left: com.example.application.customer\n" +
                          "\n" +
                          "component com.example.application.project <<Business>> [\n" +
                          "    componentName(Project)\n" +
                          "]\n" +
                          "com.example.application.project --> customer : DEPENDS_ON\n" +
                          "\n" +
                          "header\n" +
                          "<font color=red>Warning:</font>\n" +
                          "Do not use in production.\n" +
                          "endheader\n" +
                          "\n" +
                          "center footer Generated for demonstration\n" +
                          "\n" +
                          "caption figure 1\n" +
                          "\n" +
                          "legend right\n" +
                          "    | Element | Description | Package |\n" +
                          "    |customer| Customer represents... | com.example.application.customer |\n" +
                          "    |project| Project represents... | com.example.application.project |\n" +
                          "endlegend" +
                          "\n" +
                          "-----\n" +
                          "\n")
                .split("\\n");
        plantUMLLineParser = new PlantUMLLineParser(mockStore, plantUmlFileDescriptor, ParsingState.IGNORING);

        final PlantUmlClassDiagramDescriptor mockClassDiagramDescriptor = mock(PlantUmlClassDiagramDescriptor.class);
        when(mockStore.create(PlantUmlClassDiagramDescriptor.class)).thenReturn(mockClassDiagramDescriptor);
        final PlantUmlLeafDescriptor mockPlantUmlLeafDescriptor = mock(PlantUmlLeafDescriptor.class);
        when(mockStore.create(PlantUmlLeafDescriptor.class)).thenReturn(mockPlantUmlLeafDescriptor);
        final Set<PlantUmlElement> leafs = new HashSet<>();
        when(mockClassDiagramDescriptor.getLeafs()).thenReturn(leafs);
        final PlantUmlLinkRelationshipDescriptor mockRelationshipDescriptor = mock(PlantUmlLinkRelationshipDescriptor.class);
        when(mockStore.create(any(PlantUmlElement.class), eq(PlantUmlLinkRelationshipDescriptor.class), any(PlantUmlElement.class))).thenReturn(mockRelationshipDescriptor);

        stream(lines).forEach(line -> plantUMLLineParser.parseLine(line));


        verify(mockStore).create(PlantUmlClassDiagramDescriptor.class);
        verify(mockStore, times(3)).create(PlantUmlLeafDescriptor.class);
        verify(mockClassDiagramDescriptor).setTitle("backend");
        verify(mockClassDiagramDescriptor).setCaption("figure 1");
        verify(mockClassDiagramDescriptor).setLegend("| element | description | package ||customer| customer represents... | com.example.application.customer ||project| project represents... | com.example.application.project |");
        verify(mockPlantUmlLeafDescriptor, times(2)).setType("DESCRIPTION");
        verify(mockPlantUmlLeafDescriptor, times(1)).setFullName("customer");
        verify(mockPlantUmlLeafDescriptor, times(1)).setFullName(Mockito.startsWith("GMN"));
        verify(mockPlantUmlLeafDescriptor, times(1)).setFullName("com.example.application.project");
        verify(mockPlantUmlLeafDescriptor, times(1)).setDescription("componentname(customer)\n" +
                                                                    "\n" +
                                                                    "com.example.application.customer\n");
        verify(mockRelationshipDescriptor, times(2)).setHidden(false);
        verify(mockRelationshipDescriptor).setLabel("NULL");
        verify(mockRelationshipDescriptor).setType("NORMAL");
        verify(mockRelationshipDescriptor).setLabel("[depends_on]");
        verify(mockRelationshipDescriptor).setType("DASHED");

        // mock returns two times the same other mock
        // so only one entry is in the map
        assertThat(leafs.size()).isEqualTo(1);
    }

    @Test
    public void thatNoLinksToLeafsWereAddedIfNoLinksInDiagram() {
        // when there are more than threshold of components,
        // PlantUML starts to insert invisible arrows between components
        // this test checks if they were filtered out
        String[] lines = ("The Backend is structured with Domain Components in the first level. Could look like this:\n" +
                          "\n" +
                          "[\"plantuml\",\"Backend\",\"png\"]\n" +
                          "-----\n" +
                          "title First Level Components\n" +
                          "caption Level 1 Building Blocks\n" +
                          "\n" +
                          "legend right\n" +
                          "    | Element | Description |\n" +
                          "    |<#42788e>| Component |\n" +
                          "    | A --> B | A depends on B |\n" +
                          "endlegend\n" +
                          "\n" +
                          "component TechnicalService [\n" +
                          "    componentName(TechnicalService)\n" +
                          "\n" +
                          "]\n" +
                          "\n" +
                          "component Application [\n" +
                          "    componentName(Application)\n" +
                          "\n" +
                          "]\n" +
                          "\n" +
                          "component A1 [\n" +
                          "]\n" +
                          "component A2 [\n" +
                          "]\n" +
                          "component A3 [\n" +
                          "]\n" +
                          "component A4 [\n" +
                          "]\n" +
                          "component A5 [\n" +
                          "]\n" +
                          "component A6 [\n" +
                          "]\n" +
                          "\n" +
                          "-----\n" +
                          "\n")
                .split("\\n");
        plantUMLLineParser = new PlantUMLLineParser(mockStore, plantUmlFileDescriptor, ParsingState.IGNORING);

        final PlantUmlClassDiagramDescriptor mockClassDiagramDescriptor = mock(PlantUmlClassDiagramDescriptor.class);
        when(mockStore.create(PlantUmlClassDiagramDescriptor.class)).thenReturn(mockClassDiagramDescriptor);
        final PlantUmlLeafDescriptor mockPlantUmlLeafDescriptor = mock(PlantUmlLeafDescriptor.class);
        when(mockStore.create(PlantUmlLeafDescriptor.class)).thenReturn(mockPlantUmlLeafDescriptor);
        final Set<PlantUmlElement> leafs = new HashSet<>();
        when(mockClassDiagramDescriptor.getLeafs()).thenReturn(leafs);
        final PlantUmlLinkRelationshipDescriptor mockRelationshipDescriptor = mock(PlantUmlLinkRelationshipDescriptor.class);
        when(mockStore.create(any(PlantUmlElement.class), eq(PlantUmlLinkRelationshipDescriptor.class), any(PlantUmlElement.class))).thenReturn(mockRelationshipDescriptor);

        stream(lines).forEach(line -> plantUMLLineParser.parseLine(line));


        verify(mockStore).create(PlantUmlClassDiagramDescriptor.class);
        verify(mockStore, times(8)).create(PlantUmlLeafDescriptor.class);
        verify(mockClassDiagramDescriptor).setTitle("first level components");
        verify(mockClassDiagramDescriptor).setCaption("level 1 building blocks");
        verify(mockClassDiagramDescriptor, times(8)).getLeafs();
        verify(mockPlantUmlLeafDescriptor, times(1)).setFullName("technicalservice");
        verify(mockPlantUmlLeafDescriptor, times(1)).setFullName("application");

        verify(mockRelationshipDescriptor, times(0)).setHidden(false);

        // mock returns two times the same other mock
        // so only one entry is in the map
        assertThat(leafs.size()).isEqualTo(1);
    }

    @Test
    public void thatEmbeddedFileNameAndTypeWereFound() {
        final String plantUmlMarker = "[\"plantuml\",\"MainBuildingBlocks.png\",\"png\"]\n";

        final String[] parts = plantUmlMarker
                .replaceAll("\\[", "")
                .replaceAll("]", "")
                .replaceAll("\"", "")
                .replaceAll("\n", "")
                .split(",");

        assertThat(parts.length).withFailMessage("Wrong length of splitted array").isEqualTo(3);
        assertThat(parts[1]).withFailMessage("Wrong file name").isEqualTo("MainBuildingBlocks.png");
        assertThat(parts[2]).withFailMessage("Wrong file type").isEqualTo("png");

    }

}
