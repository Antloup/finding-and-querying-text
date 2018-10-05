package com.github.quadinsa5if.findingandqueryingtext.util;

import com.github.quadinsa5if.findingandqueryingtext.lang.Iter;

public interface EncoderDecoder<I> {

  I decode(Iter<Byte> msg);

  Iter<Byte> encode(I input);

}
