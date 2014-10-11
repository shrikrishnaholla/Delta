/**
 *
 * Copyright 2012-2014 Florian Schmaus
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.smackx.ping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.jivesoftware.smack.DummyConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.ThreadedDummyConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.test.util.SmackTestSuite;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smackx.InitExtensions;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jivesoftware.smackx.ping.packet.Ping;
import org.junit.Before;
import org.junit.Test;

public class PingTest extends InitExtensions {

    @Before
    public void initSmackTestSuite() {
        SmackTestSuite.init();
    }

    @Test
    public void checkProvider() throws Exception {
        // @formatter:off
        String control = "<iq from='capulet.lit' to='juliet@capulet.lit/balcony' id='s2c1' type='get'>"
                + "<ping xmlns='urn:xmpp:ping'/>" 
                + "</iq>";
        // @formatter:on
        DummyConnection con = new DummyConnection();
        
        // Enable ping for this connection
        PingManager.getInstanceFor(con);
        IQ pingRequest = (IQ) PacketParserUtils.parseStanza(control);

        assertTrue(pingRequest instanceof Ping);

        con.processPacket(pingRequest);

        Packet pongPacket = con.getSentPacket();
        assertTrue(pongPacket instanceof IQ);

        IQ pong = (IQ) pongPacket;
        assertEquals("capulet.lit", pong.getTo());
        assertEquals("s2c1", pong.getPacketID());
        assertEquals(IQ.Type.result, pong.getType());
    }

    @Test
    public void checkSendingPing() throws InterruptedException, SmackException, IOException, XMPPException {
        DummyConnection dummyCon = getAuthentiactedDummyConnection();
        PingManager pinger = PingManager.getInstanceFor(dummyCon);
        try {
            pinger.ping("test@myserver.com");
        }
        catch (SmackException e) {
            // Ignore the fact the server won't answer for this unit test.
        }

        Packet sentPacket = dummyCon.getSentPacket();
        assertTrue(sentPacket instanceof Ping);
    }

    @Test
    public void checkSuccessfulPing() throws Exception {
        ThreadedDummyConnection threadedCon = getAuthentiactedDummyConnection();
        
        PingManager pinger = PingManager.getInstanceFor(threadedCon);

        boolean pingSuccess = pinger.ping("test@myserver.com");
        
        assertTrue(pingSuccess);
        
    }

    /**
     * DummyConnection will not reply so it will timeout.
     * @throws SmackException 
     * @throws XMPPException 
     * @throws IOException 
     */
    @Test
    public void checkFailedPingOnTimeout() throws SmackException, IOException, XMPPException {
        DummyConnection dummyCon = getAuthenticatedDummyConnectionWithoutIqReplies();
        PingManager pinger = PingManager.getInstanceFor(dummyCon);

        try {
            pinger.ping("test@myserver.com");
        }
        catch (NoResponseException e) {
            return;
        }
        fail();
    }
    
    /**
     * Server returns an exception for entity.
     * @throws Exception
     */
    @Test
    public void checkFailedPingToEntityError() throws Exception {
        ThreadedDummyConnection threadedCon = getAuthentiactedDummyConnection();
        //@formatter:off
        String reply = 
                "<iq type='error' id='qrzSp-16' to='test@myserver.com'>" +
                        "<ping xmlns='urn:xmpp:ping'/>" +
                        "<error type='cancel'>" +
                            "<service-unavailable xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>" +
                        "</error>" + 
                 "</iq>";
        //@formatter:on
        IQ serviceUnavailable = (IQ) PacketParserUtils.parseStanza(reply);
        threadedCon.addIQReply(serviceUnavailable);

        PingManager pinger = PingManager.getInstanceFor(threadedCon);

        boolean pingSuccess = pinger.ping("test@myserver.com");
        
        assertFalse(pingSuccess);
    }
    
    @Test
    public void checkPingToServerSuccess() throws Exception {
        ThreadedDummyConnection con = getAuthentiactedDummyConnection();
        PingManager pinger = PingManager.getInstanceFor(con);

        boolean pingSuccess = pinger.pingMyServer();
        
        assertTrue(pingSuccess);
    }

    /**
     * Server returns an exception.
     * @throws Exception
     */
    @Test
    public void checkPingToServerError() throws Exception {
        ThreadedDummyConnection con = getAuthentiactedDummyConnection();
        //@formatter:off
        String reply = 
                "<iq type='error' id='qrzSp-16' to='test@myserver.com' from='" + con.getServiceName() + "'>" +
                        "<ping xmlns='urn:xmpp:ping'/>" +
                        "<error type='cancel'>" +
                            "<service-unavailable xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>" +
                        "</error>" + 
                 "</iq>";
        //@formatter:on
        IQ serviceUnavailable = (IQ) PacketParserUtils.parseStanza(reply);
        con.addIQReply(serviceUnavailable);

        PingManager pinger = PingManager.getInstanceFor(con);

        boolean pingSuccess = pinger.pingMyServer();
        
        assertTrue(pingSuccess);
    }
    
    @Test
    public void checkPingToServerTimeout() throws SmackException, IOException, XMPPException {
        DummyConnection con = getAuthenticatedDummyConnectionWithoutIqReplies();
        PingManager pinger = PingManager.getInstanceFor(con);

        boolean res = pinger.pingMyServer();
        assertFalse(res);
    }

    @Test
    public void checkSuccessfulDiscoRequest() throws Exception {
        ThreadedDummyConnection con = getAuthentiactedDummyConnection();
        DiscoverInfo info = new DiscoverInfo();
        info.addFeature(Ping.NAMESPACE);
        
        //@formatter:off
        String reply = 
                "<iq type='result' id='qrzSp-16' to='test@myserver.com'>" +
                        "<query xmlns='http://jabber.org/protocol/disco#info'><identity category='client' type='pc' name='Pidgin'/>" +
                            "<feature var='urn:xmpp:ping'/>" +
                        "</query></iq>";
        //@formatter:on
        IQ discoReply = (IQ) PacketParserUtils.parseStanza(reply);
        con.addIQReply(discoReply);

        PingManager pinger = PingManager.getInstanceFor(con);
        boolean pingSupported = pinger.isPingSupported("test@myserver.com");
        
        assertTrue(pingSupported);
    }

    @Test
    public void checkUnuccessfulDiscoRequest() throws Exception {
        ThreadedDummyConnection con = getAuthentiactedDummyConnection();
        DiscoverInfo info = new DiscoverInfo();
        info.addFeature(Ping.NAMESPACE);
        
        //@formatter:off
        String reply = 
                "<iq type='result' id='qrzSp-16' to='test@myserver.com'>" +
                        "<query xmlns='http://jabber.org/protocol/disco#info'><identity category='client' type='pc' name='Pidgin'/>" +
                            "<feature var='urn:xmpp:noping'/>" +
                        "</query></iq>";
        //@formatter:on
        IQ discoReply = (IQ) PacketParserUtils.parseStanza(reply);
        con.addIQReply(discoReply);

        PingManager pinger = PingManager.getInstanceFor(con);
        boolean pingSupported = pinger.isPingSupported("test@myserver.com");
        
        assertFalse(pingSupported);
    }

    private static ThreadedDummyConnection getAuthentiactedDummyConnection() throws SmackException, IOException, XMPPException {
        ThreadedDummyConnection connection = new ThreadedDummyConnection();
        connection.connect();
        connection.login("foo", "bar");
        return connection;
    }

    /**
     * The returned connection won't send replies to IQs
     * 
     * @return
     * @throws XMPPException 
     * @throws IOException 
     * @throws SmackException 
     */
    private static DummyConnection getAuthenticatedDummyConnectionWithoutIqReplies() throws SmackException, IOException, XMPPException {
        DummyConnection con = new DummyConnection();
        con.setPacketReplyTimeout(500);
        con.connect();
        con.login("foo", "bar");
        return con;
    }
}
