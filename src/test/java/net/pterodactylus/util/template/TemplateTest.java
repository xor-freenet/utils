package net.pterodactylus.util.template;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;

import junit.framework.TestCase;

/*
 * stew - TemplateTest.java - Copyright © 2010 David Roden
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Test cases for {@link Template}.
 *
 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
 */
public class TemplateTest extends TestCase {

	/**
	 * Test cases for template strings that contain no parameters.
	 *
	 * @throws IOException
	 *             if an I/O error occurs
	 * @throws TemplateException
	 */
	public void testStringTemplatesWithoutParameters() throws IOException, TemplateException {
		Template template;
		String templateString;
		StringWriter outputWriter;
		String output;

		templateString = "This is a template without parameters.";
		outputWriter = new StringWriter();
		template = new Template(new StringReader(templateString));
		template.render(outputWriter);
		output = outputWriter.toString();
		assertEquals(templateString, output);

		templateString = "This is a template without parameters but with a CR character in it.\r";
		outputWriter = new StringWriter();
		template = new Template(new StringReader(templateString));
		template.render(outputWriter);
		output = outputWriter.toString();
		assertEquals(templateString, output);

		templateString = "This is a template without parameters but with an LF character in it.\n";
		outputWriter = new StringWriter();
		template = new Template(new StringReader(templateString));
		template.render(outputWriter);
		output = outputWriter.toString();
		assertEquals(templateString, output);

		templateString = "This is a template without parameters but with a CR/LF character combination in it.\r\n";
		outputWriter = new StringWriter();
		template = new Template(new StringReader(templateString));
		template.render(outputWriter);
		output = outputWriter.toString();
		assertEquals(templateString, output);
	}

	/**
	 * Test case for template strings that contain parameters.
	 *
	 * @throws IOException
	 *             if an I/O error occurs
	 * @throws TemplateException
	 */
	public void testStringTemplatesWithParameters() throws IOException, TemplateException {
		Template template;
		String templateString;
		StringWriter outputWriter;
		String output;

		templateString = "This is a template with <%one> parameter.";
		outputWriter = new StringWriter();
		template = new Template(new StringReader(templateString));
		template.set("one", "two");
		template.render(outputWriter);
		output = outputWriter.toString();
		assertEquals("This is a template with two parameter.", output);

		templateString = "This is a template with <% one > parameter.";
		outputWriter = new StringWriter();
		template = new Template(new StringReader(templateString));
		template.set("one", "two");
		template.render(outputWriter);
		output = outputWriter.toString();
		assertEquals("This is a template with two parameter.", output);

		templateString = "This is a template with <%one> parameter, one <% left > and one <% right>.";
		outputWriter = new StringWriter();
		template = new Template(new StringReader(templateString));
		template.set("one", "two");
		template.set("left", "on top");
		template.set("right", "below");
		template.render(outputWriter);
		output = outputWriter.toString();
		assertEquals("This is a template with two parameter, one on top and one below.", output);
	}

	/**
	 * Tests for string templates that loop over collections.
	 *
	 * @throws IOException
	 *             if an I/O error occurs
	 * @throws TemplateException
	 */
	public void testStringTemplatesWithCollection() throws IOException, TemplateException {
		Template template;
		String templateString;
		StringWriter outputWriter;
		String output;
		Collection<Object> collection;
		Collection<Object> innerCollection;

		templateString = "This template repeats: <% foreach items item>item: <% item> - <% /foreach>";
		outputWriter = new StringWriter();
		template = new Template(new StringReader(templateString));
		collection = new ArrayList<Object>();
		collection.add("first");
		collection.add("second");
		template.set("items", collection);
		template.render(outputWriter);
		output = outputWriter.toString();
		assertEquals("This template repeats: item: first - item: second - ", output);

		templateString = "This template repeats: <% foreach items item>item: <% item> (<%foreach inners inner>[<%item>: <%inner>]<%/foreach>) - <% /foreach>";
		outputWriter = new StringWriter();
		template = new Template(new StringReader(templateString));
		collection = new ArrayList<Object>();
		collection.add("first");
		collection.add("second");
		template.set("items", collection);
		innerCollection = new ArrayList<Object>();
		innerCollection.add("1");
		innerCollection.add("2");
		innerCollection.add("3");
		template.set("inners", innerCollection);
		template.render(outputWriter);
		output = outputWriter.toString();
		assertEquals("This template repeats: item: first ([first: 1][first: 2][first: 3]) - item: second ([second: 1][second: 2][second: 3]) - ", output);

		templateString = "This template repeats: <% foreach items item><%foreach item inner><%inner> <%/foreach><% /foreach>";
		outputWriter = new StringWriter();
		template = new Template(new StringReader(templateString));
		collection = new ArrayList<Object>();
		innerCollection = new ArrayList<Object>();
		innerCollection.add("1");
		innerCollection.add("2");
		innerCollection.add("3");
		collection.add(innerCollection);
		innerCollection = new ArrayList<Object>();
		innerCollection.add("4");
		innerCollection.add("5");
		innerCollection.add("6");
		collection.add(innerCollection);
		template.set("items", collection);
		template.render(outputWriter);
		output = outputWriter.toString();
		assertEquals("This template repeats: 1 2 3 4 5 6 ", output);
	}

	/**
	 * Tests for string templates that loop over collections.
	 *
	 * @throws IOException
	 *             if an I/O error occurs
	 * @throws TemplateException
	 */
	public void testStringTemplatesWithCollectionAndLoopStructure() throws IOException, TemplateException {
		Template template;
		String templateString;
		StringWriter outputWriter;
		String output;
		Collection<Object> collection;
		Collection<Object> innerCollection;

		templateString = "This template repeats: <% foreach items item>item: <% loop.count> - <% /foreach>";
		outputWriter = new StringWriter();
		template = new Template(new StringReader(templateString));
		collection = new ArrayList<Object>();
		collection.add("first");
		collection.add("second");
		template.set("items", collection);
		template.render(outputWriter);
		output = outputWriter.toString();
		assertEquals("This template repeats: item: 0 - item: 1 - ", output);

		templateString = "This template repeats: <% foreach items item itemLoop>item: <% itemLoop.count> - <% /foreach>";
		outputWriter = new StringWriter();
		template = new Template(new StringReader(templateString));
		collection = new ArrayList<Object>();
		collection.add("first");
		collection.add("second");
		template.set("items", collection);
		template.render(outputWriter);
		output = outputWriter.toString();
		assertEquals("This template repeats: item: 0 - item: 1 - ", output);

		templateString = "This template repeats: <% foreach items item><% loop.count> <%foreach item inner><% loop.count> <%inner> <%/foreach><% /foreach>";
		outputWriter = new StringWriter();
		template = new Template(new StringReader(templateString));
		collection = new ArrayList<Object>();
		innerCollection = new ArrayList<Object>();
		innerCollection.add("1");
		innerCollection.add("2");
		innerCollection.add("3");
		collection.add(innerCollection);
		innerCollection = new ArrayList<Object>();
		innerCollection.add("4");
		innerCollection.add("5");
		innerCollection.add("6");
		collection.add(innerCollection);
		template.set("items", collection);
		template.render(outputWriter);
		output = outputWriter.toString();
		assertEquals("This template repeats: 0 0 1 1 2 2 3 1 0 4 1 5 2 6 ", output);
	}

	/**
	 * Tests for string templates that loop over collections.
	 *
	 * @throws IOException
	 *             if an I/O error occurs
	 * @throws TemplateException
	 */
	public void testStringTemplatesWithEmptyCollections() throws IOException, TemplateException {
		Template template;
		String templateString;
		StringWriter outputWriter;
		String output;
		Collection<Object> collection;
		Collection<Object> innerCollection;

		templateString = "This template repeats: <% foreach items item>item: <% loop.count> - <%foreachelse>nothing!<% /foreach>";
		outputWriter = new StringWriter();
		template = new Template(new StringReader(templateString));
		collection = new ArrayList<Object>();
		template.set("items", collection);
		template.render(outputWriter);
		output = outputWriter.toString();
		assertEquals("This template repeats: nothing!", output);
	}

	/**
	 * Tests for string templates that loop over collections.
	 *
	 * @throws IOException
	 *             if an I/O error occurs
	 * @throws TemplateException
	 */
	public void testStringTemplatesWithCollectionsFirst() throws IOException, TemplateException {
		Template template;
		String templateString;
		StringWriter outputWriter;
		String output;
		Collection<Object> collection;
		Collection<Object> innerCollection;

		templateString = "This template repeats: <% foreach items item><%first>first <%/first>item: <% loop.count> - <%foreachelse>nothing!<% /foreach>";
		outputWriter = new StringWriter();
		template = new Template(new StringReader(templateString));
		collection = new ArrayList<Object>();
		collection.add(1);
		collection.add(2);
		template.set("items", collection);
		template.render(outputWriter);
		output = outputWriter.toString();
		assertEquals("This template repeats: first item: 0 - item: 1 - ", output);
	}

	/**
	 * Tests for string templates that loop over collections.
	 *
	 * @throws IOException
	 *             if an I/O error occurs
	 * @throws TemplateException
	 */
	public void testStringTemplatesWithCollectionsLast() throws IOException, TemplateException {
		Template template;
		String templateString;
		StringWriter outputWriter;
		String output;
		Collection<Object> collection;
		Collection<Object> innerCollection;

		templateString = "This template repeats: <% foreach items item><%first>first <%/first>item: <% loop.count> - <%last>end<%/last><%foreachelse>nothing!<% /foreach>";
		outputWriter = new StringWriter();
		template = new Template(new StringReader(templateString));
		collection = new ArrayList<Object>();
		collection.add(1);
		collection.add(2);
		template.set("items", collection);
		template.render(outputWriter);
		output = outputWriter.toString();
		assertEquals("This template repeats: first item: 0 - item: 1 - end", output);
	}

	/**
	 * Tests for string templates that uses a custom accessor.
	 *
	 * @throws IOException
	 *             if an I/O error occurs
	 * @throws TemplateException
	 */
	public void testStringTemplatesWithAccessors() throws IOException, TemplateException {
		Template template;
		String templateString;
		StringWriter outputWriter;
		String output;
		Collection<Object> collection;
		Accessor callableAccessor = new Accessor() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			@SuppressWarnings("unchecked")
			public Object get(Object object, String member) {
				try {
					return ((Callable<String>) object).call();
				} catch (Exception e1) {
					/* ignore. */
				}
				return null;
			}
		};

		templateString = "List: <%foreach items item><%item.call> <%/foreach>";
		outputWriter = new StringWriter();
		template = new Template(new StringReader(templateString));
		template.addAccessor(Callable.class, callableAccessor);
		collection = new ArrayList<Object>();
		collection.add(new Callable<String>() {

			public String call() {
				return "Yay!";
			}
		});
		collection.add(new Callable<String>() {

			public String call() {
				return "Hooray!";
			}
		});
		template.set("items", collection);
		template.render(outputWriter);
		output = outputWriter.toString();
		assertEquals("List: Yay! Hooray! ", output);
	}

	/**
	 * Tests for string templates that contain a collection and the <%notfirst>
	 * directive.
	 *
	 * @throws IOException
	 *             if an I/O error occurs
	 * @throws TemplateException
	 */
	public void testStringTemplatesWithCollectionNotFirst() throws IOException, TemplateException {
		Template template;
		String templateString;
		StringWriter outputWriter;
		String output;
		Collection<Object> collection;

		templateString = "List: <%foreach items item><%first><%item><%/first><%notfirst>, <%item><%/notfirst><%/foreach>.";
		outputWriter = new StringWriter();
		template = new Template(new StringReader(templateString));
		collection = new ArrayList<Object>();
		collection.add(1);
		collection.add(2);
		collection.add(3);
		collection.add(4);
		template.set("items", collection);
		template.render(outputWriter);
		output = outputWriter.toString();
		assertEquals("List: 1, 2, 3, 4.", output);
	}

	/**
	 * Tests for string templates that contain a collection and the <%notlast>
	 * directive.
	 *
	 * @throws IOException
	 *             if an I/O error occurs
	 * @throws TemplateException
	 */
	public void testStringTemplatesWithCollectionNotLast() throws IOException, TemplateException {
		Template template;
		String templateString;
		StringWriter outputWriter;
		String output;
		Collection<Object> collection;

		templateString = "List: <%foreach items item><%item><%notlast>, <%/notlast><%last>.<%/last><%/foreach>";
		outputWriter = new StringWriter();
		template = new Template(new StringReader(templateString));
		collection = new ArrayList<Object>();
		collection.add(1);
		collection.add(2);
		collection.add(3);
		collection.add(4);
		template.set("items", collection);
		template.render(outputWriter);
		output = outputWriter.toString();
		assertEquals("List: 1, 2, 3, 4.", output);
	}

	/**
	 * Tests for string templates that contain <%if>.
	 *
	 * @throws IOException
	 *             if an I/O error occurs
	 * @throws TemplateException
	 */
	public void testStringTemplatesWithIf() throws IOException, TemplateException {
		Template template;
		String templateString;
		StringWriter outputWriter;
		String output;

		templateString = "A: <%if a>true<%/if>";
		outputWriter = new StringWriter();
		template = new Template(new StringReader(templateString));
		template.set("a", true);
		template.render(outputWriter);
		output = outputWriter.toString();
		assertEquals("A: true", output);

		templateString = "A: <%if a>true<%/if>";
		outputWriter = new StringWriter();
		template = new Template(new StringReader(templateString));
		template.set("a", false);
		template.render(outputWriter);
		output = outputWriter.toString();
		assertEquals("A: ", output);

		templateString = "A: <%if a>true<%if b>true<%/if><%/if>";
		outputWriter = new StringWriter();
		template = new Template(new StringReader(templateString));
		template.set("a", true);
		template.set("b", true);
		template.render(outputWriter);
		output = outputWriter.toString();
		assertEquals("A: truetrue", output);

		templateString = "A: <%if a>true<%if b>true<%/if><%/if>";
		outputWriter = new StringWriter();
		template = new Template(new StringReader(templateString));
		template.set("a", false);
		template.set("b", true);
		template.render(outputWriter);
		output = outputWriter.toString();
		assertEquals("A: ", output);

		templateString = "A: <%if a>true<%else>false<%/if>";
		outputWriter = new StringWriter();
		template = new Template(new StringReader(templateString));
		template.set("a", true);
		template.render(outputWriter);
		output = outputWriter.toString();
		assertEquals("A: true", output);

		templateString = "A: <%if a>true<%else>false<%/if>";
		outputWriter = new StringWriter();
		template = new Template(new StringReader(templateString));
		template.set("a", false);
		template.render(outputWriter);
		output = outputWriter.toString();
		assertEquals("A: false", output);

		templateString = "A: <%if a>(a)<%elseif b>(b)<%else>false<%/if>";
		outputWriter = new StringWriter();
		template = new Template(new StringReader(templateString));
		template.set("a", false);
		template.set("b", true);
		template.render(outputWriter);
		output = outputWriter.toString();
		assertEquals("A: (b)", output);

		templateString = "A: <%if a>(a)<%elseif b>(b)<%elseif c>(c)<%else>false<%/if>";
		outputWriter = new StringWriter();
		template = new Template(new StringReader(templateString));
		template.set("a", false);
		template.set("b", true);
		template.set("c", true);
		template.render(outputWriter);
		output = outputWriter.toString();
		assertEquals("A: (b)", output);
	}

}
