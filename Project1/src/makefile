JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	Status.java \
	RFCData.java \
	P2SRequest.java \
	P2SResponse.java \
	P2PRequest.java \
	P2SResponse.java \
	P2PCommunication.java \
	P2SCommunication.java \
	Client.java \
	Server.java 

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class