package org.purl.wf4ever.robundle.fs;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.After;
import org.junit.Before;
import org.purl.wf4ever.robundle.Bundles;

public class Helper {
	protected BundleFileSystem fs;

	@Before
	public void makeFS() throws IOException {
		fs = BundleFileSystemProvider.newFileSystemFromTemporary();
	}

	@After
	public void closeAndDeleteFS() throws IOException {
		fs.close();
		Path source = fs.getSource();
		Files.deleteIfExists(source);
		if (source.getParent().getFileName().toString().startsWith("robundle")) {
			Bundles.deleteRecursively(source.getParent());
		}
	}
}
