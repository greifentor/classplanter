package de.ollie.blueprints.codereader.java;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

import de.ollie.blueprints.codereader.java.antlr.Java8BaseListener;
import de.ollie.blueprints.codereader.java.antlr.Java8Parser.AnnotationContext;
import de.ollie.blueprints.codereader.java.antlr.Java8Parser.ClassBodyContext;
import de.ollie.blueprints.codereader.java.antlr.Java8Parser.ClassBodyDeclarationContext;
import de.ollie.blueprints.codereader.java.antlr.Java8Parser.ClassDeclarationContext;
import de.ollie.blueprints.codereader.java.antlr.Java8Parser.ClassMemberDeclarationContext;
import de.ollie.blueprints.codereader.java.antlr.Java8Parser.ClassModifierContext;
import de.ollie.blueprints.codereader.java.antlr.Java8Parser.CompilationUnitContext;
import de.ollie.blueprints.codereader.java.antlr.Java8Parser.ElementValueContext;
import de.ollie.blueprints.codereader.java.antlr.Java8Parser.ElementValuePairContext;
import de.ollie.blueprints.codereader.java.antlr.Java8Parser.ElementValuePairListContext;
import de.ollie.blueprints.codereader.java.antlr.Java8Parser.FieldDeclarationContext;
import de.ollie.blueprints.codereader.java.antlr.Java8Parser.FieldModifierContext;
import de.ollie.blueprints.codereader.java.antlr.Java8Parser.FormalParameterContext;
import de.ollie.blueprints.codereader.java.antlr.Java8Parser.FormalParameterListContext;
import de.ollie.blueprints.codereader.java.antlr.Java8Parser.FormalParametersContext;
import de.ollie.blueprints.codereader.java.antlr.Java8Parser.ImportDeclarationContext;
import de.ollie.blueprints.codereader.java.antlr.Java8Parser.LastFormalParameterContext;
import de.ollie.blueprints.codereader.java.antlr.Java8Parser.MarkerAnnotationContext;
import de.ollie.blueprints.codereader.java.antlr.Java8Parser.MethodDeclarationContext;
import de.ollie.blueprints.codereader.java.antlr.Java8Parser.MethodDeclaratorContext;
import de.ollie.blueprints.codereader.java.antlr.Java8Parser.MethodHeaderContext;
import de.ollie.blueprints.codereader.java.antlr.Java8Parser.MethodModifierContext;
import de.ollie.blueprints.codereader.java.antlr.Java8Parser.NormalAnnotationContext;
import de.ollie.blueprints.codereader.java.antlr.Java8Parser.NormalClassDeclarationContext;
import de.ollie.blueprints.codereader.java.antlr.Java8Parser.PackageDeclarationContext;
import de.ollie.blueprints.codereader.java.antlr.Java8Parser.PackageNameContext;
import de.ollie.blueprints.codereader.java.antlr.Java8Parser.PackageOrTypeNameContext;
import de.ollie.blueprints.codereader.java.antlr.Java8Parser.ResultContext;
import de.ollie.blueprints.codereader.java.antlr.Java8Parser.SingleElementAnnotationContext;
import de.ollie.blueprints.codereader.java.antlr.Java8Parser.SingleStaticImportDeclarationContext;
import de.ollie.blueprints.codereader.java.antlr.Java8Parser.SingleTypeImportDeclarationContext;
import de.ollie.blueprints.codereader.java.antlr.Java8Parser.StaticImportOnDemandDeclarationContext;
import de.ollie.blueprints.codereader.java.antlr.Java8Parser.TypeDeclarationContext;
import de.ollie.blueprints.codereader.java.antlr.Java8Parser.TypeImportOnDemandDeclarationContext;
import de.ollie.blueprints.codereader.java.antlr.Java8Parser.TypeNameContext;
import de.ollie.blueprints.codereader.java.antlr.Java8Parser.UnannTypeContext;
import de.ollie.blueprints.codereader.java.antlr.Java8Parser.VariableDeclaratorContext;
import de.ollie.blueprints.codereader.java.antlr.Java8Parser.VariableDeclaratorIdContext;
import de.ollie.blueprints.codereader.java.antlr.Java8Parser.VariableDeclaratorListContext;
import de.ollie.blueprints.codereader.java.antlr.Java8Parser.VariableModifierContext;
import de.ollie.blueprints.codereader.java.model.Annotation;
import de.ollie.blueprints.codereader.java.model.ClassDeclaration;
import de.ollie.blueprints.codereader.java.model.CompilationUnit;
import de.ollie.blueprints.codereader.java.model.ElementValuePair;
import de.ollie.blueprints.codereader.java.model.FieldDeclaration;
import de.ollie.blueprints.codereader.java.model.FormalParameter;
import de.ollie.blueprints.codereader.java.model.ImportDeclaration;
import de.ollie.blueprints.codereader.java.model.MethodDeclaration;
import de.ollie.blueprints.codereader.java.model.Modifier;

/**
 * The listener which converts the compilation unit from the parser tree walk.
 *
 * @author ollie (13.04.2020)
 */
public class JavaCodeConverterListener extends Java8BaseListener {

	private static final String UNKNOWN = "UNKNOWN";

	private CompilationUnit compilationUnit;

	public JavaCodeConverterListener(CompilationUnit compilationUnit) {
		super();
		this.compilationUnit = compilationUnit;
	}

	@Override
	public void enterCompilationUnit(CompilationUnitContext ctx) {
		// printChildren("", ctx);
		readPackageDeclaration(ctx);
		readImportDeclarations(ctx);
		readClassTypeDeclarations(ctx);
	}

	private void readPackageDeclaration(CompilationUnitContext ctx) {
		findChildByClass(ctx, PackageDeclarationContext.class).ifPresent(//
				pdc -> findChildByClass(pdc, PackageNameContext.class).ifPresent(//
						pnc -> compilationUnit.setPackageName(pnc.getText()) //
				) //
		);
	}

	private void readImportDeclarations(CompilationUnitContext ctx) {
		for (ImportDeclarationContext idc : findChildsByClass(ctx, ImportDeclarationContext.class)) {
			for (SingleStaticImportDeclarationContext ssidc : findChildsByClass(idc,
					SingleStaticImportDeclarationContext.class)) {
				findChildByClass(ssidc, TypeNameContext.class).ifPresent( //
						tnc -> {
							List<TerminalNodeImpl> tnis = findChildsByClass(ssidc, TerminalNodeImpl.class);
							compilationUnit.addImportDeclarations( //
									new ImportDeclaration() //
											.setImportedObject(tnis.get(tnis.size() - 2).getText()) //
											.setQualifiedName(tnc.getText()) //
											.setStaticImport(true));
						} //
				);
			}
			for (StaticImportOnDemandDeclarationContext sioddc : findChildsByClass(idc,
					StaticImportOnDemandDeclarationContext.class)) {
				findChildByClass(sioddc, TypeNameContext.class).ifPresent( //
						tnc -> compilationUnit.addImportDeclarations( //
								new ImportDeclaration() //
										.setQualifiedName(tnc.getText()) //
										.setSingleTypeImport(false) //
										.setStaticImport(true) //
						) //
				);
			}
			for (SingleTypeImportDeclarationContext stidc : findChildsByClass(idc,
					SingleTypeImportDeclarationContext.class)) {
				findChildByClass(stidc, TypeNameContext.class).ifPresent( //
						tnc -> compilationUnit.addImportDeclarations( //
								new ImportDeclaration() //
										.setQualifiedName(tnc.getText()) //
						) //
				);
			}
			for (TypeImportOnDemandDeclarationContext tioddc : findChildsByClass(idc,
					TypeImportOnDemandDeclarationContext.class)) {
				findChildByClass(tioddc, PackageOrTypeNameContext.class).ifPresent( //
						potnc -> compilationUnit.addImportDeclarations( //
								new ImportDeclaration() //
										.setQualifiedName(potnc.getText()) //
										.setSingleTypeImport(false) //
						) //
				);
			}
		}
	}

	private void readClassTypeDeclarations(CompilationUnitContext ctx) {
		for (TypeDeclarationContext tdc : findChildsByClass(ctx, TypeDeclarationContext.class)) {
			for (ClassDeclarationContext cdc : findChildsByClass(tdc, ClassDeclarationContext.class)) {
				for (NormalClassDeclarationContext ncdc : findChildsByClass(cdc, NormalClassDeclarationContext.class)) {
					findNextChildByClassAndContent(ncdc, TerminalNodeImpl.class, "class").ifPresentOrElse( //
							tni -> {
								ClassDeclaration classDeclaration = new ClassDeclaration() //
										.addAnnotations(getAnnotations(ncdc, ClassModifierContext.class)) //
										.addFields(getFields(ncdc)) //
										.addMethods(getMethods(ncdc))
										.addModifiers(getModifiers(ncdc, ClassModifierContext.class)) //
										.setName(tni.getText()) //
								;
								this.compilationUnit.addTypeDeclarations(classDeclaration);
							}, //
							() -> {
								throw new JavaCodeConverterException("ERROR: not found: 'class' in: " + ncdc.getText(),
										null);
							});
				}
			}
		}
	}

	private <T extends ParserRuleContext> Annotation[] getAnnotations(ParserRuleContext prc, Class<T> cls) {
		List<Annotation> l = new ArrayList<>();
		for (T mc : findChildsByClass(prc, cls)) {
			for (AnnotationContext ac : findChildsByClass(mc, AnnotationContext.class)) {
				findChildByClass(ac, MarkerAnnotationContext.class).ifPresent( //
						mac -> findChildByClass(mac, TypeNameContext.class).ifPresent( //
								tnc -> l.add(new Annotation().setName(tnc.getText())) //
						) //
				);
				findChildByClass(ac, SingleElementAnnotationContext.class).ifPresent( //
						seac -> findChildByClass(seac, TypeNameContext.class).ifPresent( //
								tnc -> l.add(new Annotation().setName(tnc.getText()).setValue( //
										findChildByClass(seac, ElementValueContext.class) //
												.map(ElementValueContext::getText) //
												.get() //
								)) //
						) //
				);
				findChildByClass(ac, NormalAnnotationContext.class).ifPresent( //
						nac -> findChildByClass(nac, TypeNameContext.class).ifPresent( //
								tnc -> {
									Annotation annotation = new Annotation().setName(tnc.getText());
									l.add(annotation);
									findChildByClass(nac, ElementValuePairListContext.class).ifPresent( //
											evplc -> findChildsByClass(evplc, ElementValuePairContext.class) //
													.forEach(evpc -> {
														String key = findChildByClass(evpc, TerminalNodeImpl.class)
																.get().getText();
														String value = findChildByClass(evpc, ElementValueContext.class)
																.get().getText();
														annotation.addElementValues(new ElementValuePair() //
																.setKey(key) //
																.setValue(value) //
														);
													}) //
									);
								}) //
				);
			}
		}
		return l.toArray(new Annotation[0]);
	}

	private FieldDeclaration[] getFields(ParserRuleContext prc) {
		List<FieldDeclaration> l = new ArrayList<>();
		for (ClassBodyContext cbc : findChildsByClass(prc, ClassBodyContext.class)) {
			for (ClassBodyDeclarationContext cbdc : findChildsByClass(cbc, ClassBodyDeclarationContext.class)) {
				for (ClassMemberDeclarationContext cmdc : findChildsByClass(cbdc,
						ClassMemberDeclarationContext.class)) {
					for (FieldDeclarationContext fdc : findChildsByClass(cmdc, FieldDeclarationContext.class)) {
						Annotation[] annotations = getAnnotations(fdc, FieldModifierContext.class);
						Modifier[] modifier = getModifiers(fdc, FieldModifierContext.class);
						String type = getType(fdc);
						for (String name : getVariableNames(fdc)) {
							l.add(new FieldDeclaration() //
									.addAnnotations(annotations) //
									.addModifiers(modifier) //
									.setName(name) //
									.setType(type) //
							);
						}
					}
				}
			}
		}
		return l.toArray(new FieldDeclaration[0]);
	}

	private MethodDeclaration[] getMethods(ParserRuleContext prc) {
		List<MethodDeclaration> l = new ArrayList<>();
		findChildByClass(prc, ClassBodyContext.class).ifPresent( //
				cbc -> findChildsByClass(cbc, ClassBodyDeclarationContext.class).forEach( //
						cbdc -> findChildsByClass(cbdc, ClassMemberDeclarationContext.class).forEach( //
								cmdc -> findChildsByClass(cmdc, MethodDeclarationContext.class).forEach( //
										mdc -> findChildByClass(mdc, MethodHeaderContext.class).ifPresent( //
												mhc -> l.add(new MethodDeclaration() //
														.addAnnotations(
																getAnnotations(mdc, MethodModifierContext.class)) //
														.addFormalParameters(getFormalParameters(mhc)) //
														.addModifiers(getModifiers(mdc, MethodModifierContext.class)) //
														.setName(getMethodName(mhc)) //
														.setReturnType(getReturnType(mhc)) //
												) //
										) //
								) //
						) //
				) //
		);
		return l.toArray(new MethodDeclaration[0]);
	}

	private FormalParameter[] getFormalParameters(MethodHeaderContext mhc) {
		List<FormalParameter> l = new ArrayList<>();
		findChildsByClass(mhc, MethodDeclaratorContext.class).forEach( //
				mdc -> findChildsByClass(mdc, FormalParameterListContext.class).forEach( //
						fplc -> {
							findChildsByClass(fplc, FormalParametersContext.class).forEach( //
									fpcs -> findChildsByClass(fpcs, FormalParameterContext.class).forEach( //
											fpc -> l.add(getFormalParameter(fpc))) //
							);
							findChildsByClass(fplc, LastFormalParameterContext.class).forEach( //
									lfpc -> findChildsByClass(lfpc, FormalParameterContext.class).forEach( //
											fpc -> l.add(getFormalParameter(fpc))) //
							);
						} //
				) //
		);
		return l.toArray(new FormalParameter[0]);
	}

	private FormalParameter getFormalParameter(FormalParameterContext fpc) {
		return new FormalParameter() //
				.addAnnotations(getAnnotations(fpc, VariableModifierContext.class)) //
				.setName(getVariableName(fpc)) //
				.setType(getType(fpc));
	}

	private String getReturnType(MethodHeaderContext mhc) {
		return findChildByClass(mhc, ResultContext.class) //
				.map(ResultContext::getText) //
				.orElse(UNKNOWN); //
	}

	private String getMethodName(MethodHeaderContext mhc) {
		return findChildByClass(mhc, MethodDeclaratorContext.class) //
				.map(mdc -> findChildByClass(mdc, TerminalNodeImpl.class) //
						.map(TerminalNodeImpl::getText) //
						.orElse(UNKNOWN) //
				) //
				.orElse(UNKNOWN); //
	}

	private <T extends ParserRuleContext> Modifier[] getModifiers(ParserRuleContext prc, Class<T> cls) {
		List<Modifier> l = new ArrayList<>();
		for (T mc : findChildsByClass(prc, cls)) {
			for (TerminalNodeImpl tni : findChildsByClass(mc, TerminalNodeImpl.class)) {
				String value = tni.getText();
				if (value.equals("abstract")) {
					l.add(Modifier.ABSTRACT);
				} else if (value.equals("private")) {
					l.add(Modifier.PRIVATE);
				} else if (value.equals("protected")) {
					l.add(Modifier.PROTECTED);
				} else if (value.equals("public")) {
					l.add(Modifier.PUBLIC);
				}
			}
		}
		return l.toArray(new Modifier[0]);
	}

	private String getType(ParserRuleContext prc) {
		return findChildByClass(prc, UnannTypeContext.class) //
				.map(UnannTypeContext::getText) //
				.orElse(null);
	}

	private String[] getVariableNames(ParserRuleContext prc) {
		List<String> l = new ArrayList<>();
		for (VariableDeclaratorListContext cdlc : findChildsByClass(prc, VariableDeclaratorListContext.class)) {
			for (VariableDeclaratorContext vdc : findChildsByClass(cdlc, VariableDeclaratorContext.class)) {
				l.add(getVariableName(vdc));
			}
		}
		return l.toArray(new String[0]);
	}

	private String getVariableName(ParserRuleContext prc) {
		return findChildByClass(prc, VariableDeclaratorIdContext.class) //
				.map(VariableDeclaratorIdContext::getText) //
				.orElse(UNKNOWN);
	}

	private static <T> Optional<T> findNextChildByClassAndContent(ParserRuleContext ctx, Class<T> cls, String content) {
		for (int i = 0, leni = ctx.getChildCount(); i < leni; i++) {
			if ((ctx.getChild(i).getClass() == cls) && ctx.getChild(i).getText().equals(content)) {
				return Optional.of((T) ctx.getChild(i + 1));
			}
		}
		return Optional.empty();
	}

	private static <T> Optional<T> findChildByClass(ParseTree ctx, Class<T> cls) {
		for (int i = 0, leni = ctx.getChildCount(); i < leni; i++) {
			if (ctx.getChild(i).getClass() == cls) {
				return Optional.of((T) ctx.getChild(i));
			}
		}
		return Optional.empty();
	}

	private static <T> List<T> findChildsByClass(ParseTree ctx, Class<T> cls) {
		List<T> l = new ArrayList<>(/* ctx.getChildCount() */);
		for (int i = 0, leni = ctx.getChildCount(); i < leni; i++) {
			if (ctx.getChild(i).getClass() == cls) {
				l.add((T) ctx.getChild(i));
			}
		}
		return l;
	}

//	static void printChildren(String indent, ParserRuleContext prc) {
//		System.out.println(indent + " > " + prc.getClass().getSimpleName() + " = " + prc.getText());
//		for (int i = 0, leni = prc.getChildCount(); i < leni; i++) {
//			if (prc.getChild(i) instanceof ParserRuleContext) {
//				printChildren("  " + indent, (ParserRuleContext) prc.getChild(i));
//			} else {
//				System.out.println(indent + " - " + i + " > " + prc.getChild(i).getClass().getSimpleName() + " > "
//						+ prc.getChild(i).getText());
//			}
//		}
//	}
//
//	static void printChildrenFlat(String indent, ParserRuleContext prc) {
//		System.out.println(indent + " > " + prc.getClass().getSimpleName() + " = " + prc.getText());
//		for (int i = 0, leni = prc.getChildCount(); i < leni; i++) {
//			System.out.println(indent + " - " + i + " > " + prc.getChild(i).getClass().getSimpleName() + " > "
//					+ prc.getChild(i).getText());
//		}
//	}

}