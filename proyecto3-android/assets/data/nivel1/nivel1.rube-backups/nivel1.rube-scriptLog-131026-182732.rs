//Started script log at 10/26/13 18:07:41

getBody(4).select();
getFixture(4).select();
getBody(4).deselect();
getBody(5).select();
getBody(5).deselect();
getBody(3).select();
getBody(3).deselect();
getBody(11).select();
getBody(11).deselect();
getBody(12).select();
getBody(12).deselect();
getBody(19).select();
getBody(19).deselect();
getBody(20).select();
getBody(20).deselect();
setCursor(20.0273, 0.408408);
addBody(30, '{"awake":true,"type":"dynamic"}');
getBody(30).addFixture(30, '{"density":1,"shapes":[{"radius":0,"type":"polygon"}],"friction":0.2,"vertices":{"x":[-0.5,0.5,0.5,-0.5],"y":[-0.5,-0.5,0.5,0.5]}}');
getBody(30).setPosition(20.0273,0.408408);
getBody(30).select();
getBody(30).setPosition(20.0273,0.408408);
{
	fixture _rube_redoFixture = getFixture(30);
	_rube_redoFixture.setVertex(0,-2.25,-0.5);
	_rube_redoFixture.setVertex(1,2.25,-0.5);
	_rube_redoFixture.setVertex(2,2.25,0.5);
	_rube_redoFixture.setVertex(3,-2.25,0.5);
}
getBody(30).setPosition(20.5953,0.516593);
getBody(30).setType(0);
getFixture(4).deselect();
getFixture(30).select();
getFixture(30).setSensor(2);
getFixture(30).deselect();
getFixture(30).select();
getFixture(30).deselect();
getFixture(30).select();
getFixture(30).deselect();
getFixture(1).select();
getFixture(1).setFilterMaskBits(65503);
getFixture(1).setFilterMaskBits(65535);
getFixture(1).setFilterMaskBits(65534);
getFixture(1).setFilterMaskBits(65535);
getFixture(1).deselect();
