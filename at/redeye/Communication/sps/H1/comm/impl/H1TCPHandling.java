package at.redeye.Communication.sps.H1.comm.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Vector;

import org.apache.log4j.Logger;

import at.redeye.Communication.sps.H1.comm.IH1CommListener;
import at.redeye.Communication.sps.H1.comm.IH1Communication;
import at.redeye.Communication.sps.H1.comm.UpdateReason;

import com.sun.corba.se.impl.ior.ByteBuffer;

public class H1TCPHandling implements IH1Communication {

    private ConnectionPhase phase = ConnectionPhase.Disconnected;
    private H1ConnectionDefinition conndef;
    private Socket s = null;
    private DataOutputStream out = null;
    private DataInputStream in = null;
    private Logger logger = null;
    private static H1TCPHandling _myInstance;
    private String message = "";
    private Vector<IH1CommListener> allListener = new Vector<IH1CommListener>();

    private H1TCPHandling(H1ConnectionDefinition conndef, Logger logger) {
        this.conndef = conndef;
        this.logger = logger;
    }

    public static synchronized H1TCPHandling getInstance(
            H1ConnectionDefinition conndef, Logger logger) {
        if (_myInstance == null) {
            _myInstance = new H1TCPHandling(conndef, logger);
        }
        return _myInstance;
    }

    public ConnectionPhase getConnectionPhase() {
        return phase;
    }

    public void setStatus(ConnectionPhase status) {
        this.phase = status;
    }

    private int handleH1Request(ConnectionPhase currentPhase) {


        switch (currentPhase) {

            case DisConnectionAttempt:
                logger.info("Attempting disconnect...");
                try {
                    if (in != null) {
                        in.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                    if (s != null) {
                        s.close();
                    }
                } catch (IOException ioe) {
                    message = ioe.getMessage();
                    logger.error("Failed to close H1: " + message);
                    return (-1);
                }
                phase = ConnectionPhase.Disconnected;
                updateListener(UpdateReason.CONNECTION_PHASE_CHANGED, null, message);
                // STOP
                break;

            case Disconnected:
                message = " --- ";
                phase = ConnectionPhase.TCPConnectionAttempt;
                updateListener(UpdateReason.CONNECTION_PHASE_CHANGED, null, message);
                return handleH1Request(phase);

            case TCPConnectionAttempt:
            	logger.info("Attempting TCP connection...");
                message = " --- ";
                try {
                    connectTCP(conndef);
                    phase = ConnectionPhase.TCPConnected;
                    logger.info("TCP ready");

                } catch (UnknownHostException uhe) {
                    phase = ConnectionPhase.DisConnectionAttempt;
                    message = uhe.getMessage();
                    logger.error(message);

                } catch (IOException ioe) {
                    phase = ConnectionPhase.DisConnectionAttempt;
                    message = ioe.getMessage();
                    logger.error(message);

                }
                updateListener(UpdateReason.CONNECTION_PHASE_CHANGED, null, message);
                

                return handleH1Request(phase);

            case TCPConnected:
                phase = ConnectionPhase.H1ConnectionAttempt;
                updateListener(UpdateReason.CONNECTION_PHASE_CHANGED, null, message);
                return handleH1Request(phase);

            case H1ConnectionAttempt:
                try {
                    connectH1(conndef);
                    phase = ConnectionPhase.H1Connected;

                } catch (IOException ioe) {

                    phase = ConnectionPhase.DisConnectionAttempt;
                    message = ioe.getMessage();
                    logger.error(message);

                } catch (H1ConnectionException e) {
                    phase = ConnectionPhase.DisConnectionAttempt;
                    message = e.getMessage();
                    logger.error(message);

                }
                updateListener(UpdateReason.CONNECTION_PHASE_CHANGED, null, message);
                return handleH1Request(phase);

            case H1Connected:
                break;

        }
        return 1;

    }

    private void connectTCP(H1ConnectionDefinition conndef)
            throws UnknownHostException, IOException {

        s = new Socket(conndef.getHostname(), conndef.getPort());
        s.setSoTimeout(MAX_SO_TIMEOUT);
        s.setTrafficClass(0);

        logger.info("Socket: " + s.getInetAddress().toString() + ": " + s.getPort());
        if (s != null) {

            out = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
            in = new DataInputStream(
                    new BufferedInputStream(s.getInputStream()));

        } else {
            logger.error("Socket is not connected!");
        }

    }

    private void connectH1(H1ConnectionDefinition conndef) throws IOException,
            H1ConnectionException {

        OSIConnectionFrame ocf = new OSIConnectionFrame();

        ocf.code = OSIConnectionFrame.CODE_CR;

        ocf.tp_class = 0;

        ByteBuffer bb = ocf.toByteBuffer();
        bb.trimToSize();
        int frame_len = bb.size();

        logger.info("NOTE: frame_len = " + frame_len);

        bb.append(OSIConnectionFrame.PARA_TPDU);
        bb.append((byte) 1);
        bb.append((byte) OSIConnectionFrame.POW_PDU);
        bb.append((byte) OSIConnectionFrame.PARA_MYTSAP);

        byte[] tsap = conndef.getMytsapname().getBytes();
        bb.append((byte) tsap.length);
        for (int i = 0; i < tsap.length; i++) {
            bb.append(tsap[i]);
        }

        tsap = conndef.getHosttsapname().getBytes();
        bb.append((byte) OSIConnectionFrame.PARA_HOSTTSAP);
        bb.append((byte) tsap.length);
        for (int i = 0; i < tsap.length; i++) {
            bb.append(tsap[i]);
        }

        bb.trimToSize();

        int len = bb.size();

        // Fill OSI header with length info
        ocf.osiheader.tpkt_len[0] = (byte) (len / 0x100);
        ocf.osiheader.tpkt_len[1] = (byte) (len % 0x100);
        ocf.osiheader.headlen = (byte) (len - ocf.osiheader.getLength());

        // Replace the affected bytes in ByteBuffer

        byte[] header = ocf.osiheader.toByteArray();
        byte[] arr = bb.toArray();

        for (int idx = 0; idx < header.length; idx++) {
            arr[idx] = header[idx];
        }
        StringBuilder str = new StringBuilder();
        for (int idx = 0; idx < arr.length; idx++) {
            if (idx == header.length) {
                str.append(" | ");
            }
            str.append(String.format("%02x ", arr[idx]));

        }
        logger.info("Connect: " + str.toString());
        transmit_connect(arr);
        byte[] response = null;

        response = receive(len);

        if (response != null && response.length < frame_len) {

            throw new H1ConnectionException(
                    "H1 partner response: Received too few bytes!");

        }

        OSIConnectionFrame recframe = new OSIConnectionFrame();
        recframe.initializeByBytes(response);

        if (recframe.code != OSIConnectionFrame.CODE_CC) {
            throw new H1ConnectionException(
                    "H1 partner response: Did not receive CODE_CC in code field! [" + String.format("%02x", recframe.code) + "]");
        }

        for (int i = frame_len + 1; i < response.length; i++) {
            logger.info("Checking: " + response[i] + " / " + String.format("%02x", response[i]));
            if (response[i] == OSIConnectionFrame.PARA_HOSTTSAP || response[i] == OSIConnectionFrame.PARA_MYTSAP) {
                logger.info("Found TSAP identifier!");
                break;
            }
            if (response[i] == OSIConnectionFrame.PARA_TPDU) {
                logger.info("Checking MAX_PDU: " + String.format("%02x", response[i]));
                if (response[i + 2] < 4 || response[i + 2] > 16) {
                    throw new H1ConnectionException(
                            "H1 partner response: Invalid MAX_PDU!");
                }

            }
        }
        transmitH1Answer();
        logger.info("Logon handshake finished successfully!");

    }

    @Override
    public byte[] receive(int expectedLength) throws IOException,
            SocketTimeoutException, H1ConnectionException {

        if (s == null || in == null) {
            throw new H1ConnectionException("Receive data: Not connected!");
        }

        if (expectedLength <= 0) {
            expectedLength = OSIConnectionFrame.MAX_PDU;
        }
        OSIHeader header = new OSIHeader();
        byte[] buffer = new byte[expectedLength];
        int len = in.read(buffer, 0, expectedLength);

        ByteBuffer bb = new ByteBuffer();
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < len; i++) {
            bb.append((byte) buffer[i]);
            if (i == header.getLength()) {
                str.append(" | ");
            }
            str.append(String.format("%02x ", (byte) buffer[i]));
        }
        logger.info("Receive (" + len + "): " + str.toString());
        bb.trimToSize();
        byte[] arr = bb.toArray();
        updateListener(UpdateReason.INBOUND_MESSAGE, arr, null);
        return arr;
    }

    @Override
    public void transmit(byte[] dataToSend) throws IOException, H1ConnectionException {

        if (s == null || out == null) {
            throw new H1ConnectionException("Transmit data: Not connected!");
        }
        OSIDataFrame odf = new OSIDataFrame();

        odf.code = (byte) OSIConnectionFrame.CODE_DT;
        odf.last = (byte) OSIConnectionFrame.IS_LAST;

        ByteBuffer bb = odf.toByteBuffer();
        if (dataToSend != null) {
            for (int i = 0; i < dataToSend.length; i++) {
                bb.append(dataToSend[i]);
            }
        }
        bb.trimToSize();
        int len = bb.size();

        // Fill OSI header with length info
        odf.osiheader.tpkt_len[0] = (byte) (len / 0x100);
        odf.osiheader.tpkt_len[1] = (byte) (len % 0x100);
        odf.osiheader.headlen = (byte) 2;

        StringBuilder str = new StringBuilder();

        // Replace the affected bytes in ByteBuffer

        byte[] header = odf.osiheader.toByteArray();
        byte[] arr = bb.toArray();

        for (int idx = 0; idx < header.length; idx++) {
            arr[idx] = header[idx];
        }

        for (int idx = 0; idx < arr.length; idx++) {
            if (idx == header.length) {
                str.append(" | ");
            }
            str.append(String.format("%02x ", arr[idx]));

        }
        logger.info("Transmit (" + bb.size() + "): " + str.toString());

        out.write(bb.toArray());
        out.flush();

    }

    @Override
    public int handleH1Request() {
        return handleH1Request(ConnectionPhase.Disconnected);
    }

    public void transmitH1Answer() throws IOException, H1ConnectionException {

        transmit(null);
    }

    protected void transmit_connect(byte[] dataToSend) throws IOException {

        // Attempting to send now ...
        if (out != null) {
            out.write(dataToSend);
            out.flush();

        } else {
            throw new IOException(
                    "H1 connect: Did not get OUT-stream from socket!");
        }

    }

    public void setStopRequest() {
        handleH1Request(ConnectionPhase.DisConnectionAttempt);
    }

    @Override
    public void setConnectionDefinition(H1ConnectionDefinition conndef) {
        this.conndef = conndef;
    }

    @Override
    public void addListener(IH1CommListener listener) {
        allListener.add(listener);

    }

    @Override
    public void removeListener(IH1CommListener listener) {
        allListener.remove(listener);

    }

    @Override
    public void updateListener(UpdateReason reason, byte[] data, String message) {
        for (IH1CommListener listener : allListener) {

            switch (reason) {
                case CONNECTION_PHASE_CHANGED:
                    listener.actionConnectionPhaseChanged(phase, message);
                    break;
                case OUTBOUND_MESSAGE:
                    listener.actionMessageOutbound();
                    break;
                case INBOUND_MESSAGE:
                    listener.actionMessageInbound(data);
                    break;
                default:
                    break;
            }

        }
    }
}
