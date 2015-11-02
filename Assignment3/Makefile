JFLAGS = -g -source 1.6 -target 1.6
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	AgentInfo.java \
	AI.java \
	Explorer.java \
	GameMap.java \
	AStarComparator.java \
	AStarUnit.java \
	Coords.java \
	FloodLengthReturn.java \
	FloodNode.java \
	Agent.java \
	AgentInfoImpl.java \
	AIImpl.java \
	ExplorerImpl.java \
	MapImpl.java \
	MoveCalculator.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
