import React, { useState } from 'react';
import { useLocation } from 'react-router-dom';
import { Nav } from 'react-bootstrap';
import './analysis.css';
import Tab from '../components/Tab';

function Analysis() {
  let [tab, setTab] = useState(0);
  const [show, setShow] = useState(false);
  const location = useLocation();
  const responseData = location.state?.responseData || null;

  return (
    <div>
      <div className={`analysis-container ${show ? 'shrink' : ''}`}>
        <>
          <Nav variant="tabs" defaultActiveKey="link0" className="justify-content-start">
            <Nav.Item>
              <Nav.Link eventKey="link0" active={tab === 0} onClick={() => setTab(0)}>Alignment</Nav.Link>
            </Nav.Item>
            <Nav.Item>
              <Nav.Link eventKey="link1" active={tab === 1} onClick={() => setTab(1)}>mRNA design</Nav.Link>
            </Nav.Item>
            <Nav.Item>
              <Nav.Link eventKey="link2" active={tab === 2} onClick={() => setTab(2)}>3D viewer</Nav.Link>
            </Nav.Item>
          </Nav>
          <Tab tab={tab} setTab={setTab} responseData={responseData} />
        </>
      </div>
    </div>
  );
}

export default Analysis;