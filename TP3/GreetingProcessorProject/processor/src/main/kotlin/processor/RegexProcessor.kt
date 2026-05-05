package processor

import annotations.Extract
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_23)
@SupportedAnnotationTypes("annotations.Extract")
class RegexProcessor : AbstractProcessor() {
    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        val classMethodMap = mutableMapOf<TypeElement, MutableList<ExecutableElement>>()

        // 1. Agrupar métodos por classe
        for (element in roundEnv.getElementsAnnotatedWith(Extract::class.java)) {
            if (element is ExecutableElement) {
                val enclosingClass = element.enclosingElement as TypeElement
                classMethodMap.computeIfAbsent(enclosingClass) { mutableListOf() }.add(element)
            }
        }

        // 2. Gerar a classe Extractor para cada classe encontrada
        for ((classElement, methods) in classMethodMap) {
            val packageName = processingEnv.elementUtils.getPackageOf(classElement).toString()
            val originalClassName = classElement.simpleName.toString()
            val generatedClassName = "${originalClassName}Extractor"

            val classBuilder = TypeSpec.classBuilder(generatedClassName)
                .superclass(ClassName(packageName, originalClassName))
                .addModifiers(KModifier.PUBLIC)
                .primaryConstructor(FunSpec.constructorBuilder()
                    .addParameter("input", String::class)
                    .build())
                .addSuperclassConstructorParameter("input")

            for (method in methods) {
                val regexValue = method.getAnnotation(Extract::class.java).regex
                val methodName = method.simpleName.toString()

                val methodBuilder = FunSpec.builder(methodName)
                    .addModifiers(KModifier.OVERRIDE, KModifier.PUBLIC)
                    .returns(String::class.asTypeName().copy(nullable = true))
                    .addStatement("val match = Regex(%P).find(input)", regexValue)
                    .addStatement("return match?.groupValues?.get(1)")

                classBuilder.addFunction(methodBuilder.build())
            }

            val file = FileSpec.builder(packageName, generatedClassName).addType(classBuilder.build()).build()

            // 3. Escrever o ficheiro no disco
            try {
                val kaptKotlinGeneratedDir = processingEnv.options["kapt.kotlin.generated"]
                if (kaptKotlinGeneratedDir != null) {
                    file.writeTo(File(kaptKotlinGeneratedDir))
                }
            } catch (e: Exception) {
                processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Erro ao gerar ficheiro: ${e.message}")
            }
        }
        return true
    }
}