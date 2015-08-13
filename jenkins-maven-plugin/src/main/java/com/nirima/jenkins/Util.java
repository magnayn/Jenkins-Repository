package com.nirima.jenkins;

/*
 * The MIT License
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., Kohsuke Kawaguchi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import org.apache.commons.codec.digest.DigestUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.annotation.Nonnull;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Various utility methods that don't have more proper home.
 *
 * @author Kohsuke Kawaguchi
 */
public class Util {

  // Constant number of milliseconds in various time units.
  private static final long ONE_SECOND_MS = 1000;
  private static final long ONE_MINUTE_MS = 60 * ONE_SECOND_MS;
  private static final long ONE_HOUR_MS = 60 * ONE_MINUTE_MS;
  private static final long ONE_DAY_MS = 24 * ONE_HOUR_MS;
  private static final long ONE_MONTH_MS = 30 * ONE_DAY_MS;
  private static final long ONE_YEAR_MS = 365 * ONE_DAY_MS;


  /**
   * Computes MD5 digest of the given input stream.
   *
   * @param source
   *      The stream will be closed by this method at the end of this method.
   * @return
   *      32-char wide string
   * @see DigestUtils#md5Hex(InputStream)
   */
  @Nonnull
  public static String getDigestOf(@Nonnull InputStream source) throws IOException {
    try {
      MessageDigest md5 = MessageDigest.getInstance("MD5");

      byte[] buffer = new byte[1024];
      DigestInputStream in =new DigestInputStream(source,md5);
      try {
        while(in.read(buffer)>=0)
          ; // simply discard the input
      } finally {
        in.close();
      }
      return toHexString(md5.digest());
    } catch (NoSuchAlgorithmException e) {
      throw new IOException("MD5 not installed",e);    // impossible
    }
        /* JENKINS-18178: confuses Maven 2 runner
        try {
            return DigestUtils.md5Hex(source);
        } finally {
            source.close();
        }
        */
  }

  @Nonnull
  public static String getDigestOf(@Nonnull String text) {
    try {
      return getDigestOf(new ByteArrayInputStream(text.getBytes("UTF-8")));
    } catch (IOException e) {
      throw new Error(e);
    }
  }

  /**
   * Computes the MD5 digest of a file.
   * @param file a file
   * @return a 32-character string
   * @throws IOException in case reading fails
   * @since 1.525
   */
  @Nonnull
  public static String getDigestOf(@Nonnull File file) throws IOException {
    InputStream is = new FileInputStream(file);
    try {
      return getDigestOf(new BufferedInputStream(is));
    } finally {
      is.close();
    }
  }

  /**
   * Converts a string into 128-bit AES key.
   * @since 1.308
   */
  @Nonnull
  public static SecretKey toAes128Key(@Nonnull String s) {
    try {
      // turn secretKey into 256 bit hash
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      digest.reset();
      digest.update(s.getBytes("UTF-8"));

      // Due to the stupid US export restriction JDK only ships 128bit version.
      return new SecretKeySpec(digest.digest(),0,128/8, "AES");
    } catch (NoSuchAlgorithmException e) {
      throw new Error(e);
    } catch (UnsupportedEncodingException e) {
      throw new Error(e);
    }
  }

  @Nonnull
  public static String toHexString(@Nonnull byte[] data, int start, int len) {
    StringBuilder buf = new StringBuilder();
    for( int i=0; i<len; i++ ) {
      int b = data[start+i]&0xFF;
      if(b<16)    buf.append('0');
      buf.append(Integer.toHexString(b));
    }
    return buf.toString();
  }

  @Nonnull
  public static String toHexString(@Nonnull byte[] bytes) {
    return toHexString(bytes,0,bytes.length);
  }

  @Nonnull
  public static byte[] fromHexString(@Nonnull String data) {
    byte[] r = new byte[data.length() / 2];
    for (int i = 0; i < data.length(); i += 2)
      r[i / 2] = (byte) Integer.parseInt(data.substring(i, i + 2), 16);
    return r;
  }

  }
