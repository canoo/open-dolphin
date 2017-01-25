/*
 * Copyright 2012-2017 Canoo Engineering AG.
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

package org.opendolphin.core.comm

import groovy.util.logging.Log

import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

/**
 * A json codec that uses gzip compression for requests beyond a given size.
 * In general, one can assume that it is better to just rely on the HTTP mechanisms for
 * gzipping requests and responses but in reality, not every server is able to handle that -
 * or the effort to make it run prohibitively high.
 * In order to transfer the binary (compressed) parts reliably, we use base 64 encoding
 * on top of gzip, which adds to the processing time and transfer size.
 * In the context of OpenDolphin commands, this facility compresses to between 10 and 20 percent.
 */

@Log
class ZippedJsonCodec extends JsonCodec {

    /**
     * The max amount of characters in the transfer string that will not result in compressing.
     * Whatever fits in one "packet" of the underlying infrastructure makes no sense compressing.
     * Note that depending of the String encoding, one character can result in multiple bytes for
     * transmission.
     * It makes sense to keep this value rather low.
     */
    int maxUnzippedSize = 500
    String encoding = "UTF-8"
    protected final int MIN_ENCODED_SIZE = 16 // 10 byte GZip header in base 64 encoding

    @Override
    String encode(List<Command> commands) {
        def result = super.encode(commands)
        if (result.size() <= maxUnzippedSize) {
            log.finest "not zipping since text size ${result.size()} does not exceed $maxUnzippedSize"
            return result
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(result.size())
        new GZIPOutputStream(outputStream).withWriter(encoding) { Writer writer ->
            writer.write(result)
        }

        log.finest "zipped text of size ${result.size()}"
        result =  outputStream.toByteArray().encodeBase64().toString()
        log.finest "to zip of size ${result.size()}"
        return result
    }

    @Override
    List<Command> decode(String transmitted) {
        if (transmitted.size() <= MIN_ENCODED_SIZE) return super.decode(transmitted)

        def raw = transmitted
        try {
            byte[] bytes = transmitted.decodeBase64()
            GZIPInputStream zipStream = new GZIPInputStream(new ByteArrayInputStream(bytes))
            raw = zipStream.getText(encoding)
            log.finest "unzipped transmission of size $bytes.length to text of size ${raw.size()}"
        } catch (Exception e) {
            log.fine "could not unzip transmission, assuming it is not zipped.\nText:$transmitted"
        }

        return super.decode(raw)
    }
}
