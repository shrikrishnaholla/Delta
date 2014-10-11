/**
 *
 * Copyright © 2014 Florian Schmaus
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
package org.jivesoftware.smack.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ByteUtils {

    private static MessageDigest md5Digest;

    public synchronized static byte[] md5(byte[] data) {
        if (md5Digest == null) {
            try {
                md5Digest = MessageDigest.getInstance(StringUtils.MD5);
            }
            catch (NoSuchAlgorithmException nsae) {
                // Smack wont be able to function normally if this exception is thrown, wrap it into
                // an ISE and make the user aware of the problem.
                throw new IllegalStateException(nsae);
            }
        }
        md5Digest.update(data);
        return md5Digest.digest();
    }

    public static byte[] concact(byte[] arrayOne, byte[] arrayTwo) {
        int combinedLength = arrayOne.length + arrayTwo.length;
        byte[] res = new byte[combinedLength];
        System.arraycopy(arrayOne, 0, res, 0, arrayOne.length);
        System.arraycopy(arrayTwo, 0, res, arrayOne.length, arrayTwo.length);
        return res;
    }
}
