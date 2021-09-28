package de.ollie.blueprints.codereader.java;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import de.ollie.blueprints.codereader.java.model.Annotation;
import de.ollie.blueprints.codereader.java.model.ClassDeclaration;
import de.ollie.blueprints.codereader.java.model.CompilationUnit;
import de.ollie.blueprints.codereader.java.model.ElementValuePair;
import de.ollie.blueprints.codereader.java.model.FieldDeclaration;
import de.ollie.blueprints.codereader.java.model.FormalParameter;
import de.ollie.blueprints.codereader.java.model.ImportDeclaration;
import de.ollie.blueprints.codereader.java.model.MethodDeclaration;
import de.ollie.blueprints.codereader.java.model.Modifier;

@ExtendWith(MockitoExtension.class)
public class JavaCodeConverterTest {

	@InjectMocks
	private JavaCodeConverter unitUnderTest;

	@DisplayName("Returns a null value, if a null value is passed")
	@Test
	void nullValuePassed_ReturnsANullValue() {
		assertNull(unitUnderTest.convert(null), "null value expected for a passed null value.");
	}

	@DisplayName("Returns an empty compilation unit, if an empty string is passed")
	@Test
	void emptyStringPassed_ReturnsAnEmptyCompilationUnit() {
		// Prepare
		CompilationUnit expected = new CompilationUnit();
		// Run
		CompilationUnit returned = unitUnderTest.convert("");
		// Check
		assertEquals(expected, returned);
	}

	@DisplayName("Returns correct compilation unit passing a simple class file content.")
	@Test
	void simpleClassFileContentPassed_ReturnsACorrectCompilationUnit() {
		// Prepare
		String simpleClassFileContent = "" //
				+ "class ASimpleClass {\n" //
				+ "}";
		CompilationUnit expected = new CompilationUnit() //
				.addTypeDeclarations( //
						new ClassDeclaration() //
								.setName("ASimpleClass") //
				) //
		;
		// Run
		CompilationUnit returned = unitUnderTest.convert(simpleClassFileContent);
		// Check
		assertEquals(expected, returned);
	}

	@DisplayName("Returns correct compilation unit passing a class file content with class modifier 'public'.")
	@Test
	void simpleClassFileContentWithClassModifierPublicPassed_ReturnsACorrectCompilationUnit() {
		// Prepare
		String simpleClassFileContent = "" //
				+ "public class ASimpleClass {\n" //
				+ "}";
		CompilationUnit expected = new CompilationUnit() //
				.addTypeDeclarations( //
						new ClassDeclaration() //
								.addModifiers( //
										Modifier.PUBLIC //
								) //
								.setName("ASimpleClass") //
				) //
		;
		// Run
		CompilationUnit returned = unitUnderTest.convert(simpleClassFileContent);
		// Check
		assertEquals(expected, returned);
	}

	@DisplayName("Returns correct compilation unit passing a class file content with class modifier 'public' and "
			+ "'abstract'.")
	@Test
	void simpleClassFileContentWithClassModifierPublicAndAbstractPassed_ReturnsACorrectCompilationUnit() {
		// Prepare
		String simpleClassFileContent = "" //
				+ "public abstract class ASimpleClass {\n" //
				+ "}";
		CompilationUnit expected = new CompilationUnit() //
				.addTypeDeclarations( //
						new ClassDeclaration() //
								.addModifiers( //
										Modifier.PUBLIC, //
										Modifier.ABSTRACT //
								) //
								.setName("ASimpleClass") //
				) //
		;
		// Run
		CompilationUnit returned = unitUnderTest.convert(simpleClassFileContent);
		// Check
		assertEquals(expected, returned);
	}

	@DisplayName("Returns correct compilation unit passing a class file content with two classes.")
	@Test
	void simpleClassFileContentWithTwoClassesPassed_ReturnsACorrectCompilationUnit() {
		// Prepare
		String simpleClassFileContent = "" //
				+ "public abstract class ASimpleClass {\n" //
				+ "}\n" //
				+ "\n" //
				+ "class AFriendClass {\n" //
				+ "}\n" //
		;
		CompilationUnit expected = new CompilationUnit() //
				.addTypeDeclarations( //
						new ClassDeclaration() //
								.addModifiers( //
										Modifier.PUBLIC, //
										Modifier.ABSTRACT //
								) //
								.setName("ASimpleClass"), //
						new ClassDeclaration() //
								.setName("AFriendClass") //
				) //
		;
		// Run
		CompilationUnit returned = unitUnderTest.convert(simpleClassFileContent);
		// Check
		assertEquals(expected, returned);
	}

	@DisplayName("Returns correct compilation unit passing a class file content with package declaration.")
	@Test
	void simpleClassFileContentWithAPackageDeclarationPassed_ReturnsACorrectCompilationUnit() {
		// Prepare
		String simpleClassFileContent = "" //
				+ "package fantasy.pack.age;\n" //
				+ "\n" //
				+ "public class ASimpleClass {\n" //
				+ "}\n" //
		;
		CompilationUnit expected = new CompilationUnit() //
				.addTypeDeclarations( //
						new ClassDeclaration() //
								.addModifiers( //
										Modifier.PUBLIC //
								) //
								.setName("ASimpleClass") //
				) //
				.setPackageName("fantasy.pack.age") //
		;
		// Run
		CompilationUnit returned = unitUnderTest.convert(simpleClassFileContent);
		// Check
		assertEquals(expected, returned);
	}

	@DisplayName("Returns correct compilation unit passing a class file content with import declaration for a single "
			+ "class.")
	@Test
	void simpleClassFileContentWithASingleClassImportDeclarationPassed_ReturnsACorrectCompilationUnit() {
		// Prepare
		String simpleClassFileContent = "" //
				+ "import fantasy.pack.age.AClass;\n" //
				+ "\n" //
				+ "public class ASimpleClass {\n" //
				+ "}\n" //
		;
		CompilationUnit expected = new CompilationUnit() //
				.addTypeDeclarations( //
						new ClassDeclaration() //
								.addModifiers( //
										Modifier.PUBLIC //
								) //
								.setName("ASimpleClass") //
				) //
				.addImportDeclarations( //
						new ImportDeclaration().setQualifiedName("fantasy.pack.age.AClass") //
				) //
		;
		// Run
		CompilationUnit returned = unitUnderTest.convert(simpleClassFileContent);
		// Check
		assertEquals(expected, returned);
	}

	@DisplayName("Returns correct compilation unit passing a class file content with import declaration for a whole "
			+ "package.")
	@Test
	void simpleClassFileContentWithAWholePackageImportDeclarationPassed_ReturnsACorrectCompilationUnit() {
		// Prepare
		String simpleClassFileContent = "" //
				+ "import fantasy.pack.age.*;\n" //
				+ "\n" //
				+ "public class ASimpleClass {\n" //
				+ "}\n" //
		;
		CompilationUnit expected = new CompilationUnit() //
				.addTypeDeclarations( //
						new ClassDeclaration() //
								.addModifiers( //
										Modifier.PUBLIC //
								) //
								.setName("ASimpleClass") //
				) //
				.addImportDeclarations( //
						new ImportDeclaration() //
								.setQualifiedName("fantasy.pack.age") //
								.setSingleTypeImport(false) //
				) //
		;
		// Run
		CompilationUnit returned = unitUnderTest.convert(simpleClassFileContent);
		// Check
		assertEquals(expected, returned);
	}

	@DisplayName("Returns correct compilation unit passing a class file content with static import declaration for a single "
			+ "class.")
	@Test
	void simpleClassFileContentWithAStaticSingleClassImportDeclarationPassed_ReturnsACorrectCompilationUnit() {
		// Prepare
		String simpleClassFileContent = "" //
				+ "import static fantasy.pack.age.AClass.aMethod;\n" //
				+ "\n" //
				+ "public class ASimpleClass {\n" //
				+ "}\n" //
		;
		CompilationUnit expected = new CompilationUnit() //
				.addTypeDeclarations( //
						new ClassDeclaration() //
								.addModifiers( //
										Modifier.PUBLIC //
								) //
								.setName("ASimpleClass") //
				) //
				.addImportDeclarations( //
						new ImportDeclaration() //
								.setImportedObject("aMethod") //
								.setQualifiedName("fantasy.pack.age.AClass") //
								.setStaticImport(true) //
				) //
		;
		// Run
		CompilationUnit returned = unitUnderTest.convert(simpleClassFileContent);
		// Check
		assertEquals(expected, returned);
	}

	@DisplayName("Returns correct compilation unit passing a class file content with static import declaration for a "
			+ "whole package.")
	@Test
	void simpleClassFileContentWithAStaticWholePackageImportDeclarationPassed_ReturnsACorrectCompilationUnit() {
		// Prepare
		String simpleClassFileContent = "" //
				+ "import static fantasy.pack.age.AClass.*;\n" //
				+ "\n" //
				+ "public class ASimpleClass {\n" //
				+ "}\n" //
		;
		CompilationUnit expected = new CompilationUnit() //
				.addTypeDeclarations( //
						new ClassDeclaration() //
								.addModifiers( //
										Modifier.PUBLIC //
								) //
								.setName("ASimpleClass") //
				) //
				.addImportDeclarations( //
						new ImportDeclaration() //
								.setQualifiedName("fantasy.pack.age.AClass") //
								.setSingleTypeImport(false) //
								.setStaticImport(true) //
				) //
		;
		// Run
		CompilationUnit returned = unitUnderTest.convert(simpleClassFileContent);
		// Check
		assertEquals(expected, returned);
	}

	@DisplayName("Returns correct compilation unit passing a class file content with static import declaration for a "
			+ "whole package.")
	@Test
	void simpleClassFileContentWithTwoPackageImportDeclarationsPassed_ReturnsACorrectCompilationUnit() {
		// Prepare
		String simpleClassFileContent = "" //
				+ "import static fantasy.pack.age.AClass.*;\n" //
				+ "import alles.hier.von.*;\n" //
				+ "import alles.hier.von.auch.*;\n" //
				+ "\n" //
				+ "public class ASimpleClass {\n" //
				+ "}\n" //
		;
		CompilationUnit expected = new CompilationUnit() //
				.addTypeDeclarations( //
						new ClassDeclaration() //
								.addModifiers( //
										Modifier.PUBLIC //
								) //
								.setName("ASimpleClass") //
				) //
				.addImportDeclarations( //
						new ImportDeclaration() //
								.setQualifiedName("fantasy.pack.age.AClass") //
								.setSingleTypeImport(false) //
								.setStaticImport(true), //
						new ImportDeclaration() //
								.setQualifiedName("alles.hier.von") //
								.setSingleTypeImport(false), //
						new ImportDeclaration() //
								.setQualifiedName("alles.hier.von.auch") //
								.setSingleTypeImport(false) //
				) //
		;
		// Run
		CompilationUnit returned = unitUnderTest.convert(simpleClassFileContent);
		// Check
		assertEquals(expected, returned);
	}

	@DisplayName("Returns correct compilation unit passing a class file content with annotations for theclass.")
	@Test
	void simpleClassFileContentWithSimpleAnnotationForTheClassPassed_ReturnsACorrectCompilationUnit() {
		// Prepare
		String simpleClassFileContent = "" //
				+ "@Data\n" //
				+ "@Generated\n" //
				+ "public class ASimpleClass {\n" //
				+ "}\n" //
		;
		CompilationUnit expected = new CompilationUnit() //
				.addTypeDeclarations( //
						new ClassDeclaration() //
								.addAnnotations( //
										new Annotation().setName("Data"), //
										new Annotation().setName("Generated") //
								) //
								.addModifiers( //
										Modifier.PUBLIC //
								) //
								.setName("ASimpleClass") //
				) //
		;
		// Run
		CompilationUnit returned = unitUnderTest.convert(simpleClassFileContent);
		// Check
		assertEquals(expected, returned);
	}

	@DisplayName("Returns correct compilation unit passing a class file content with annotations for the class which "
			+ "have a simple parameters.")
	@Test
	void simpleClassFileContentWithAnnotationWithSimpleParametersForTheClassPassed_ReturnsACorrectCompilationUnit() {
		// Prepare
		String simpleClassFileContent = "" //
				+ "@AnAnnotation(\"bla\")\n" //
				+ "@RequestMapping(\"api/v1\")\n" //
				+ "public class ASimpleClass {\n" //
				+ "}\n" //
		;
		CompilationUnit expected = new CompilationUnit() //
				.addTypeDeclarations( //
						new ClassDeclaration() //
								.addAnnotations( //
										new Annotation().setName("AnAnnotation").setValue("\"bla\""), //
										new Annotation().setName("RequestMapping").setValue("\"api/v1\"") //
								) //
								.addModifiers( //
										Modifier.PUBLIC //
								) //
								.setName("ASimpleClass") //
				) //
		;
		// Run
		CompilationUnit returned = unitUnderTest.convert(simpleClassFileContent);
		// Check
		assertEquals(expected, returned);
	}

	@DisplayName("Returns correct compilation unit passing a class file content with annotations for the class which "
			+ "have a parameter list.")
	@Test
	void simpleClassFileContentWithAnnotationWithAListOfParametersForTheClassPassed_ReturnsACorrectCompilationUnit() {
		// Prepare
		String simpleClassFileContent = "" //
				+ "@AnAnnotation(param0 = 4711, param1 = \"a string\")\n" //
				+ "public class ASimpleClass {\n" //
				+ "}\n" //
		;
		CompilationUnit expected = new CompilationUnit() //
				.addTypeDeclarations( //
						new ClassDeclaration() //
								.addAnnotations( //
										new Annotation() //
												.addElementValues( //
														new ElementValuePair().setKey("param0").setValue("4711"), //
														new ElementValuePair().setKey("param1").setValue("\"a string\"") //
												) //
												.setName("AnAnnotation")//
								) //
								.addModifiers( //
										Modifier.PUBLIC //
								) //
								.setName("ASimpleClass") //
				) //
		;
		// Run
		CompilationUnit returned = unitUnderTest.convert(simpleClassFileContent);
		// Check
		assertEquals(expected, returned);
	}

	@DisplayName("Returns correct compilation unit passing a class file content a simple field declaration.")
	@Test
	void simpleClassFileContentWithASimpleFieldDeclarationPassed_ReturnsACorrectCompilationUnit() {
		// Prepare
		String simpleClassFileContent = "" //
				+ "public class AClass {\n" //
				+ "\n" //
				+ "    private int member;\n" //
				+ "\n" //
				+ "}\n" //
		;
		CompilationUnit expected = new CompilationUnit() //
				.addTypeDeclarations( //
						new ClassDeclaration() //
								.addFields(new FieldDeclaration() //
										.addModifiers( //
												Modifier.PRIVATE //
										) //
										.setName("member") //
										.setType("int") //
								) //
								.addModifiers( //
										Modifier.PUBLIC //
								) //
								.setName("AClass") //
				) //
		;
		// Run
		CompilationUnit returned = unitUnderTest.convert(simpleClassFileContent);
		// Check
		assertEquals(expected, returned);
	}

	@DisplayName("Returns correct compilation unit passing a class file content with multiple field declarations.")
	@Test
	void simpleClassFileContentWithMultipleFieldDeclarationsPassed_ReturnsACorrectCompilationUnit() {
		// Prepare
		String simpleClassFileContent = "" //
				+ "public class AClass {\n" //
				+ "\n" //
				+ "    private int member, anotherMember = 0;\n" //
				+ "    protected String aThirdMember = null;\n" //
				+ "\n" //
				+ "}\n" //
		;
		CompilationUnit expected = new CompilationUnit() //
				.addTypeDeclarations( //
						new ClassDeclaration() //
								.addFields( //
										new FieldDeclaration() //
												.addModifiers( //
														Modifier.PRIVATE //
												) //
												.setName("member") //
												.setType("int"), //
										new FieldDeclaration() //
												.addModifiers( //
														Modifier.PRIVATE //
												) //
												.setName("anotherMember") //
												.setType("int"), //
										new FieldDeclaration() //
												.addModifiers( //
														Modifier.PROTECTED //
												) //
												.setName("aThirdMember") //
												.setType("String") //
								) //
								.addModifiers( //
										Modifier.PUBLIC //
								) //
								.setName("AClass") //
				) //
		;
		// Run
		CompilationUnit returned = unitUnderTest.convert(simpleClassFileContent);
		// Check
		assertEquals(expected, returned);
	}

	@DisplayName("Returns correct compilation unit passing a class file content with a field declaration with annotations.")
	@Test
	void simpleClassFileContentWithFieldDeclarationWithAnnotationsPassed_ReturnsACorrectCompilationUnit() {
		// Prepare
		String simpleClassFileContent = "" //
				+ "public class AClass {\n" //
				+ "\n" //
				+ "    @AnAnnotation(name = \"annotationName\") @AnotherAnnotation private int member;\n" //
				+ "\n" //
				+ "}\n" //
		;
		CompilationUnit expected = new CompilationUnit() //
				.addTypeDeclarations( //
						new ClassDeclaration() //
								.addFields(new FieldDeclaration() //
										.addAnnotations( //
												new Annotation() //
														.addElementValues(new ElementValuePair() //
																.setKey("name") //
																.setValue("\"annotationName\"") //
														) //
														.setName("AnAnnotation"), //
												new Annotation() //
														.setName("AnotherAnnotation") //
										) //
										.addModifiers( //
												Modifier.PRIVATE //
										) //
										.setName("member") //
										.setType("int") //
								) //
								.addModifiers( //
										Modifier.PUBLIC //
								) //
								.setName("AClass") //
				) //
		;
		// Run
		CompilationUnit returned = unitUnderTest.convert(simpleClassFileContent);
		// Check
		assertEquals(expected, returned);
	}

	@DisplayName("Returns correct compilation unit passing a class file content with a simple method declaration.")
	@Test
	void simpleClassFileContentWithASimpleMethodPassed_ReturnsACorrectCompilationUnit() {
		// Prepare
		String simpleClassFileContent = "" //
				+ "public class AClass {\n" //
				+ "\n" //
				+ "    public void AMethod() {\n" //
				+ "        bla();\n" //
				+ "    }\n" //
				+ "\n" //
				+ "}\n" //
		;
		CompilationUnit expected = new CompilationUnit() //
				.addTypeDeclarations( //
						new ClassDeclaration() //
								.addMethods(new MethodDeclaration() //
										.addModifiers(Modifier.PUBLIC) //
										.setName("AMethod") //
										.setReturnType("void") //
								) //
								.addModifiers( //
										Modifier.PUBLIC //
								) //
								.setName("AClass") //
				) //
		;
		// Run
		CompilationUnit returned = unitUnderTest.convert(simpleClassFileContent);
		// Check
		assertEquals(expected, returned);
	}

	@DisplayName("Returns correct compilation unit passing a class file content with a method declaration with annotations.")
	@Test
	void simpleClassFileContentWithMethodDeclarationWithAnnotationsPassed_ReturnsACorrectCompilationUnit() {
		// Prepare
		String simpleClassFileContent = "" //
				+ "public class AClass {\n" //
				+ "\n" //
				+ "    @Getter @AnAnnotation(parameter = 4711) public void AMethod() {\n" //
				+ "        bla();\n" //
				+ "    }\n" //
				+ "\n" //
				+ "}\n" //
		;
		CompilationUnit expected = new CompilationUnit() //
				.addTypeDeclarations( //
						new ClassDeclaration() //
								.addMethods(new MethodDeclaration() //
										.addAnnotations( //
												new Annotation() //
														.setName("Getter"), //
												new Annotation() //
														.addElementValues(new ElementValuePair() //
																.setKey("parameter") //
																.setValue("4711") //
														) //
														.setName("AnAnnotation") //
										) //
										.addModifiers(Modifier.PUBLIC) //
										.setName("AMethod") //
										.setReturnType("void") //
								) //
								.addModifiers( //
										Modifier.PUBLIC //
								) //
								.setName("AClass") //
				) //
		;
		// Run
		CompilationUnit returned = unitUnderTest.convert(simpleClassFileContent);
		// Check
		assertEquals(expected, returned);
	}

	@DisplayName("Returns correct compilation unit passing a class file content with a method declaration with parameters with annotations.")
	@Test
	void simpleClassFileContentWithMethodWithParametersPassed_ReturnsACorrectCompilationUnit() {
		// Prepare
		String simpleClassFileContent = "" //
				+ "public class AClass {\n" //
				+ "\n" //
				+ "    public void AMethod(int aParam, @Name @RequestParam(name = \"id\") String name) {\n" //
				+ "        bla();\n" //
				+ "    }\n" //
				+ "\n" //
				+ "}\n" //
		;
		CompilationUnit expected = new CompilationUnit() //
				.addTypeDeclarations( //
						new ClassDeclaration() //
								.addMethods(new MethodDeclaration() //
										.addModifiers(Modifier.PUBLIC) //
										.addFormalParameters( //
												new FormalParameter() //
														.setName("aParam") //
														.setType("int"), //
												new FormalParameter() //
														.addAnnotations( //
																new Annotation() //
																		.setName("Name"), //
																new Annotation() //
																		.addElementValues(new ElementValuePair() //
																				.setKey("name") //
																				.setValue("\"id\"") //
																		) //
																		.setName("RequestParam") //
														) //
														.setName("name") //
														.setType("String") //
										) //
										.setName("AMethod") //
										.setReturnType("void") //
								) //
								.addModifiers( //
										Modifier.PUBLIC //
								) //
								.setName("AClass") //
				) //
		;
		// Run
		CompilationUnit returned = unitUnderTest.convert(simpleClassFileContent);
		// Check
		assertEquals(expected, returned);
	}

}