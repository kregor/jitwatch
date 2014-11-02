/*
 * Copyright (c) 2013, 2014 Chris Newland.
 * Licensed under https://github.com/AdoptOpenJDK/jitwatch/blob/master/LICENSE-BSD
 * Instructions: https://github.com/AdoptOpenJDK/jitwatch/wiki
 */
package org.adoptopenjdk.jitwatch.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.adoptopenjdk.jitwatch.core.HotSpotLogParser;
import org.adoptopenjdk.jitwatch.core.ILogParser;
import org.adoptopenjdk.jitwatch.core.JITWatchConstants;
import org.adoptopenjdk.jitwatch.model.SplitLog;
import org.junit.Test;

public class TestLogSplitting
{
	@Test
	public void testCanParseAssemblyAndNMethodMangled() throws Exception
	{    		
		String nmethodTag = "<nmethod compile_id='1' compiler='C1' level='3' entry='0x00007fb5ad0fe420' size='2504' address='0x00007fb5ad0fe290' relocation_offset='288'/>";

		String[] lines = new String[] {
				"[Loaded java.lang.String from /home/chris/jdk1.9.0/jre/lib/rt.jar]",
				"Decoding compiled method 0x00007f7d73364190:",
				"Code:",
				"[Disassembling for mach=&apos;i386:x86-64&apos;]",
				"[Entry Point]",
				"[Verified Entry Point]",
				"[Constants]",
				"  # {method} &apos;length&apos; &apos;()I&apos; in &apos;java/lang/String&apos;",
				"  0x00007f7d733642e0: callq  0x00007f7d77e276f0  ;   {runtime_call}",
				"[Deopt Handler Code]",
				"0x00007fb5ad0fe95c: movabs $0x7fb5ad0fe95c,%r10  ;   {section_word}",
				"0x00007fb5ad0fe966: push   %r10",
				"0x00007fb5ad0fe968: jmpq   0x00007fb5ad047100  ;   {runtime_call}",
				"0x00007fb5ad0fe96d: hlt",
				"0x00007fb5ad0fe96e: hlt",
				"0x00007fb5ad0fe96f: hlt " + nmethodTag,
				"<writer thread='140418643298048'/>" };

		Path path = writeLinesToTempFileAndReturnPath(lines);

		ILogParser parser = new HotSpotLogParser(UnitTestUtil.getNoOpJITListener());

		parser.processLogFile(path.toFile(), UnitTestUtil.getNoOpParseErrorListener());

		SplitLog log = parser.getSplitLog();

		assertEquals(15, log.getAssemblyLines().size());
		assertEquals(2, log.getLogCompilationLines().size());
	}

	private Path writeLinesToTempFileAndReturnPath(String[] lines) throws IOException
	{
		StringBuilder builder = new StringBuilder();

		for (String line : lines)
		{
			builder.append(line).append(JITWatchConstants.S_NEWLINE);
		}

		Path path = Files.createTempFile("testsplit", ".log");

		Files.write(path, builder.toString().getBytes(StandardCharsets.UTF_8));

		return path;
	}

	@Test
	public void testHeaderXMLTextNodes() throws IOException
	{
		String[] lines = new String[] {
				"<?xml version='1.0' encoding='UTF-8'?>",
				"<hotspot_log version='160 1' process='6868' time_ms='1412577606738'>",
				"<vm_version>",
				"<TweakVM/>",
				"<name>",
				"Java HotSpot(TM) 64-Bit Server VM",
				"</name>",
				"<release>",
				"1.9.0-ea-b32",
				"</release>",
				"<info>",
				"Java HotSpot(TM) 64-Bit Server VM (1.9.0-ea-b32) for linux-amd64 JRE (1.9.0-ea-b32), built on Sep 25 2014 00:27:31 by &quot;java_re&quot; with ",
				"gcc 4.8.2",
				"</info>",
				"</vm_version>",
				"<vm_arguments>",
				"<args>",
				"-XX:+UnlockDiagnosticVMOptions -XX:+TraceClassLoading -XX:+LogCompilation -XX:-TieredCompilation -XX:+PrintAssembly -XX:-UseCompressedOops ",
				"</args>",
				"<command>",
				"org.adoptopenjdk.jitwatch.demo.MakeHotSpotLog",
				"</command>",
				"<launcher>",
				"SUN_STANDARD",
				"</launcher>",
				"<properties>",
				"java.vm.specification.name=Java Virtual Machine Specification",
				"java.vm.version=1.9.0-ea-b32",
				"java.vm.name=Java HotSpot(TM) 64-Bit Server VM",
				"java.vm.info=mixed mode, sharing",
				"java.ext.dirs=/home/chris/jdk1.9.0/jre/lib/ext:/usr/java/packages/lib/ext",
				"java.endorsed.dirs=/home/chris/jdk1.9.0/jre/lib/endorsed",
				"sun.boot.library.path=/home/chris/jdk1.9.0/jre/lib/amd64",
				"java.library.path=/usr/java/packages/lib/amd64:/usr/lib64:/lib64:/lib:/usr/lib",
				"java.home=/home/chris/jdk1.9.0/jre",
				"java.class.path=target/classes:lib/logback-classic-1.1.2.jar:lib/logback-core-1.1.2.jar:lib/slf4j-api-1.7.7.jar",
				"sun.boot.class.path=/home/chris/jdk1.9.0/jre/lib/resources.jar:/home/chris/jdk1.9.0/jre/lib/rt.jar:/home/chris/jdk1.9.0/jre/lib/jsse.jar:/home/",
				"chris/jdk1.9.0/jre/lib/jce.jar:/home/chris/jdk1.9.0/jre/lib/charsets.jar:/home/chris/jdk1.9.0/jre/lib/jfr.jar:/home/chris/jdk1.9.0/jre/classes",
				"java.vm.specification.vendor=Oracle Corporation",
				"java.vm.specification.version=1.9",
				"java.vm.vendor=Oracle Corporation",
				"sun.java.command=org.adoptopenjdk.jitwatch.demo.MakeHotSpotLog",
				"sun.java.launcher=SUN_STANDARD",
				"</properties>",
				"</vm_arguments>",
				"<tty>",
				"<writer thread='139788786714368'/>",
				"[Opened /home/chris/jdk1.9.0/jre/lib/rt.jar]",
				"[Loaded java.lang.Object from /home/chris/jdk1.9.0/jre/lib/rt.jar]",
				"[Loaded java.io.Serializable from /home/chris/jdk1.9.0/jre/lib/rt.jar]",
				"[Loaded java.lang.Comparable from /home/chris/jdk1.9.0/jre/lib/rt.jar]",
				"[Loaded java.lang.CharSequence from /home/chris/jdk1.9.0/jre/lib/rt.jar]",
				"[Loaded java.lang.String from /home/chris/jdk1.9.0/jre/lib/rt.jar]",
				"[Loaded java.lang.reflect.AnnotatedElement from /home/chris/jdk1.9.0/jre/lib/rt.jar]",
				"[Loaded java.lang.reflect.GenericDeclaration from /home/chris/jdk1.9.0/jre/lib/rt.jar]",
				"[Loaded java.lang.reflect.Type from /home/chris/jdk1.9.0/jre/lib/rt.jar]" };

		Path path = writeLinesToTempFileAndReturnPath(lines);

		ILogParser parser = new HotSpotLogParser(UnitTestUtil.getNoOpJITListener());

		parser.processLogFile(path.toFile(), UnitTestUtil.getNoOpParseErrorListener());

		SplitLog log = parser.getSplitLog();

		assertEquals(45, log.getHeaderLines().size());
		
		// <tty> not counted as it envelopes the main XML
		assertEquals(1, log.getLogCompilationLines().size());
		assertEquals(8, log.getClassLoaderLines().size());
		
		assertEquals("org.adoptopenjdk.jitwatch.demo.MakeHotSpotLog", parser.getVMCommand());
	}
}