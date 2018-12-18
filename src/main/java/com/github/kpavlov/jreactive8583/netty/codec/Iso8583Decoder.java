package com.github.kpavlov.jreactive8583.netty.codec;

import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.MessageFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.text.ParseException;
import java.util.List;

public class Iso8583Decoder extends ByteToMessageDecoder {
    private static final Logger logger = LoggerFactory.getLogger(Iso8583Decoder.class);

    private final MessageFactory messageFactory;

    public Iso8583Decoder(MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
    }

    /**
     * @implNote Message body starts immediately, no length header,
     * see <code>"lengthFieldFameDecoder"</code> in
     * {@link com.github.kpavlov.jreactive8583.netty.pipeline.Iso8583ChannelInitializer#initChannel}
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List out) throws Exception {
        if (!byteBuf.isReadable()) {
            return;
        }
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);

        final IsoMessage isoMessage = messageFactory.parseMessage(bytes, 0);
        logger.debug("Received Message: {}", HexCodec.hexEncode(bytes, 0, bytes.length));
        if (isoMessage != null) {
            //noinspection unchecked
            out.add(isoMessage);
        } else {
            throw new ParseException("Can't parse ISO8583 message", 0);
        }
    }
}
