package com.github.simy4.poc;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.Architectures;
import com.tngtech.archunit.library.GeneralCodingRules;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;

@AnalyzeClasses(
    packages = ArchUnitTest.ROOT_PACKAGE,
    importOptions = ImportOption.DoNotIncludeTests.class)
class ArchUnitTest {
  static final String ROOT_PACKAGE = "com.github.simy4.poc";

  @ArchTest
  ArchRule noGenericExceptions = GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;

  @ArchTest ArchRule noFieldInjection = GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION;

  @ArchTest
  ArchRule layeredArchitecture =
      Architectures.layeredArchitecture()
          .consideringOnlyDependenciesInLayers()
          .layer("model")
          .definedBy(ROOT_PACKAGE + ".model..")
          .layer("controllers")
          .definedBy(ROOT_PACKAGE + ".controllers..")
          .layer("repositories")
          .definedBy(ROOT_PACKAGE + ".repositories..")
          .whereLayer("model")
          .mayNotAccessAnyLayer()
          .whereLayer("controllers")
          .mayNotBeAccessedByAnyLayer()
          .whereLayer("repositories")
          .mayOnlyBeAccessedByLayers("controllers");

  @ArchTest
  public static final ArchRule noCircularPackages =
      SlicesRuleDefinition.slices().matching(ROOT_PACKAGE + ".(**)").should().beFreeOfCycles();
}
