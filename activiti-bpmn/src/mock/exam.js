export var examStr = `
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<definitions xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:activiti="http://activiti.org/bpmn" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:dc="http://www.omg.org/spec/DD/20100524/DC" xmlns:di="http://www.omg.org/spec/DD/20100524/DI" xmlns:tns="http://www.activiti.org/testm1564997427166" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" expressionLanguage="http://www.w3.org/1999/XPath" id="m1564997427166" name="" targetNamespace="http://www.activiti.org/testm1564997427166" typeLanguage="http://www.w3.org/2001/XMLSchema">
  <process activiti:candidateStarterUsers="${startUser}" id="myProcess_1" isClosed="false" isExecutable="true" name="请假流程" processType="None">
    <startEvent id="_2" name="StartEvent"/>
    <endEvent id="_3" name="EndEvent"/>
    <userTask activiti:candidateGroups="affair" activiti:exclusive="true" id="_4" name="人事审核"/>
    <exclusiveGateway gatewayDirection="Unspecified" id="_5" name="ExclusiveGateway"/>
    <userTask activiti:candidateGroups="president" activiti:exclusive="true" id="_6" name="总经理审核"/>
    <userTask activiti:candidateGroups="manager" activiti:exclusive="true" id="_7" name="经理审核"/>
    <sequenceFlow id="_8" sourceRef="_2" targetRef="_4"/>
    <sequenceFlow id="_9" name="超3天" sourceRef="_5" targetRef="_6">
      <conditionExpression xsi:type="tFormalExpression"><![CDATA[${days > 3}]]></conditionExpression>
    </sequenceFlow>
    <sequenceFlow id="_10" sourceRef="_6" targetRef="_3"/>
    <sequenceFlow id="_11" name="同意" sourceRef="_4" targetRef="_5">
      <conditionExpression xsi:type="tFormalExpression"><![CDATA[${agree=='yes'}]]></conditionExpression>
    </sequenceFlow>
    <sequenceFlow id="_12" name="小三天" sourceRef="_5" targetRef="_7">
      <conditionExpression xsi:type="tFormalExpression"><![CDATA[${days <= 3}]]></conditionExpression>
    </sequenceFlow>
    <sequenceFlow id="_13" sourceRef="_7" targetRef="_3"/>
    <sequenceFlow id="_14" name="不同意" sourceRef="_4" targetRef="_3">
      <conditionExpression xsi:type="tFormalExpression"><![CDATA[${agree=='no'}]]></conditionExpression>
    </sequenceFlow>
  </process>
  <bpmndi:BPMNDiagram documentation="background=#3C3F41;count=1;horizontalcount=1;orientation=0;width=842.4;height=1195.2;imageableWidth=832.4;imageableHeight=1185.2;imageableX=5.0;imageableY=5.0" id="Diagram-_1" name="New Diagram">
    <bpmndi:BPMNPlane bpmnElement="myProcess_1">
      <bpmndi:BPMNShape bpmnElement="_2" id="Shape-_2">
        <dc:Bounds height="32.0" width="32.0" x="230.0" y="95.0"/>
        <bpmndi:BPMNLabel>
          <dc:Bounds height="32.0" width="32.0" x="0.0" y="0.0"/>
        </bpmndi:BPMNLabel>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape bpmnElement="_3" id="Shape-_3">
        <dc:Bounds height="32.0" width="32.0" x="235.0" y="465.0"/>
        <bpmndi:BPMNLabel>
          <dc:Bounds height="32.0" width="32.0" x="0.0" y="0.0"/>
        </bpmndi:BPMNLabel>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape bpmnElement="_4" id="Shape-_4">
        <dc:Bounds height="55.0" width="85.0" x="205.0" y="180.0"/>
        <bpmndi:BPMNLabel>
          <dc:Bounds height="55.0" width="85.0" x="0.0" y="0.0"/>
        </bpmndi:BPMNLabel>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape bpmnElement="_5" id="Shape-_5" isMarkerVisible="false">
        <dc:Bounds height="32.0" width="32.0" x="230.0" y="275.0"/>
        <bpmndi:BPMNLabel>
          <dc:Bounds height="32.0" width="32.0" x="0.0" y="0.0"/>
        </bpmndi:BPMNLabel>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape bpmnElement="_6" id="Shape-_6">
        <dc:Bounds height="55.0" width="85.0" x="205.0" y="370.0"/>
        <bpmndi:BPMNLabel>
          <dc:Bounds height="55.0" width="85.0" x="0.0" y="0.0"/>
        </bpmndi:BPMNLabel>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape bpmnElement="_7" id="Shape-_7">
        <dc:Bounds height="55.0" width="85.0" x="380.0" y="265.0"/>
        <bpmndi:BPMNLabel>
          <dc:Bounds height="55.0" width="85.0" x="0.0" y="0.0"/>
        </bpmndi:BPMNLabel>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNEdge bpmnElement="_13" id="BPMNEdge__13" sourceElement="_7" targetElement="_3">
        <di:waypoint x="425.0" y="320.0"/>
        <di:waypoint x="425.0" y="400.0"/>
        <di:waypoint x="267.0" y="481.0"/>
        <bpmndi:BPMNLabel>
          <dc:Bounds height="0.0" width="0.0" x="0.0" y="0.0"/>
        </bpmndi:BPMNLabel>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge bpmnElement="_12" id="BPMNEdge__12" sourceElement="_5" targetElement="_7">
        <di:waypoint x="262.0" y="291.0"/>
        <di:waypoint x="380.0" y="292.5"/>
        <bpmndi:BPMNLabel>
          <dc:Bounds height="0.0" width="0.0" x="0.0" y="0.0"/>
        </bpmndi:BPMNLabel>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge bpmnElement="_14" id="BPMNEdge__14" sourceElement="_4" targetElement="_3">
        <di:waypoint x="205.0" y="207.5"/>
        <di:waypoint x="115.0" y="360.0"/>
        <di:waypoint x="235.0" y="481.0"/>
        <bpmndi:BPMNLabel>
          <dc:Bounds height="0.0" width="0.0" x="0.0" y="0.0"/>
        </bpmndi:BPMNLabel>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge bpmnElement="_8" id="BPMNEdge__8" sourceElement="_2" targetElement="_4">
        <di:waypoint x="246.0" y="127.0"/>
        <di:waypoint x="246.0" y="180.0"/>
        <bpmndi:BPMNLabel>
          <dc:Bounds height="0.0" width="0.0" x="0.0" y="0.0"/>
        </bpmndi:BPMNLabel>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge bpmnElement="_9" id="BPMNEdge__9" sourceElement="_5" targetElement="_6">
        <di:waypoint x="246.0" y="307.0"/>
        <di:waypoint x="246.0" y="370.0"/>
        <bpmndi:BPMNLabel>
          <dc:Bounds height="0.0" width="0.0" x="0.0" y="0.0"/>
        </bpmndi:BPMNLabel>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge bpmnElement="_11" id="BPMNEdge__11" sourceElement="_4" targetElement="_5">
        <di:waypoint x="246.0" y="235.0"/>
        <di:waypoint x="246.0" y="275.0"/>
        <bpmndi:BPMNLabel>
          <dc:Bounds height="0.0" width="0.0" x="0.0" y="0.0"/>
        </bpmndi:BPMNLabel>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge bpmnElement="_10" id="BPMNEdge__10" sourceElement="_6" targetElement="_3">
        <di:waypoint x="251.0" y="425.0"/>
        <di:waypoint x="251.0" y="465.0"/>
        <bpmndi:BPMNLabel>
          <dc:Bounds height="0.0" width="0.0" x="0.0" y="0.0"/>
        </bpmndi:BPMNLabel>
      </bpmndi:BPMNEdge>
    </bpmndi:BPMNPlane>
  </bpmndi:BPMNDiagram>
</definitions>
`