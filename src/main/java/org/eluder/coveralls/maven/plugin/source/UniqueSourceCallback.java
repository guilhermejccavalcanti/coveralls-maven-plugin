package org.eluder.coveralls.maven.plugin.source;

/*
 * #[license]
 * coveralls-maven-plugin
 * %%
 * Copyright (C) 2013 - 2014 Tapio Rautonen
 * %%
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
 * %[license]
 */

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.eluder.coveralls.maven.plugin.ProcessingException;
import org.eluder.coveralls.maven.plugin.domain.Source;

/**
 * Source callback that tracks passed by source files and provides only unique
 * source files to the delegate. Note that the implementation is not thread
 * safe so the {@link #onSource(org.eluder.coveralls.maven.plugin.domain.Source)}
 * can be called only from single thread concurrently.
 */
public class UniqueSourceCallback implements SourceCallback {
    
    private static final String LINES_SEPARATOR = "#";
    
    private final Set<String> cache = new HashSet<String>();
    private final SourceCallback delegate;

    public UniqueSourceCallback(final SourceCallback delegate) {
        this.delegate = delegate;
    }

    @Override
    public void onSource(final Source source) throws ProcessingException, IOException {
        String key = getKey(source);
        if (!cache.contains(key)) {
            cache.add(key);
            delegate.onSource(source);
        }
    }
    
    private String getKey(final Source source) {
        return source.getFullName() + LINES_SEPARATOR + getRelevantLines(source);
    }
    
    private int getRelevantLines(final Source source) {
        int relevant = 0;
        for (Integer cov : source.getCoverage()) {
            if (cov != null) {
                relevant++;
            }
        }
        return relevant;
    }
}
