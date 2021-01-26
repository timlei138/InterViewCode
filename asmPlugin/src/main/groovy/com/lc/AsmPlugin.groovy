package com.lc

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.android.build.api.transform.TransformOutputProvider
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.compress.utils.IOUtils
import org.gradle.api.Plugin
import org.gradle.api.Project;
import com.android.build.gradle.AppExtension
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry


class AsmPlugin extends Transform implements Plugin<Project>{

    @Override
    void apply(Project project) {
        def android = project.extensions.getByType(AppExtension.class)
        android.registerTransform(this)
    }

    @Override
    String getName() {
        return "ASMPlugin"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        //super.transform(transformInvocation)
        println("---------ASMPlugin transform start---------------")
        def start = System.currentTimeMillis()
        Collection<TransformInput> inputs = transformInvocation.inputs
        TransformOutputProvider outputProvider = transformInvocation.outputProvider
        //删除旧的输出
        if (outputProvider != null){
            outputProvider.deleteAll()
        }
        //遍历inputs
        inputs.each {input ->
            input.directoryInputs.each { directoryInput ->
                handleDirectoryInput(directoryInput,outputProvider)
            }
            input.jarInputs.each { jarInput ->
                handleJarInput(jarInput,outputProvider)
            }
        }

        def time = (System.currentTimeMillis() - start) / 1000
        println('"---------ASMPlugin transform end---------------"')
        println("ASMPlugin cost $time s")

    }

    /**
     * 处理目录下的class文件
     * @param directoryInput
     * @param provider
     */
    static void handleDirectoryInput(DirectoryInput directoryInput,TransformOutputProvider provider){
        //是否为目录
        if (directoryInput.file.isDirectory()){
            //列出目录所有的文件
            directoryInput.file.eachFileRecurse { file ->
                if (isClassFile(name)){
                    println("-------------------- handle class file:<$name> --------------------")
                    ClassReader classReader = new ClassReader(file.bytes)
                    ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                    ClassVisitor classVisitor = new ActivityClassVisitor(classWriter)
                    classReader.accept(classVisitor, org.objectweb.asm.ClassReader.EXPAND_FRAMES)
                    byte[] bytes = classWriter.toByteArray()
                    FileOutputStream fileOutputStream = new FileOutputStream(file.parentFile.absolutePath + File.separator + name)
                    fileOutputStream.write(bytes)
                    fileOutputStream.close()
                }
            }
        }
        def dest = provider.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
        FileUtils.copyDirectory(directoryInput.file, dest)
    }

    /**
     * 处理Jar中的class文件
     * @param directoryInput
     * @param provider
     */
    static void handleJarInput(JarInput jarInput, TransformOutputProvider provider){
        if (jarInput.file.getAbsolutePath().endsWith(".jar")) {
            //重名名输出文件,因为可能同名,会覆盖
            def jarName = jarInput.name
            def md5Name = DigestUtils.md5Hex(jarInput.file.absolutePath)
            if (jarName.endsWith(".jar")) {
                jarName = jarName.substring(0, jarName.length() - 4)
            }
            JarFile jarFile = new JarFile(jarInput.file)
            Enumeration enumeration = jarFile.entries()
            File tempFile = new File(jarInput.file.parent + File.separator + "temp.jar")
            //避免上次的缓存被重复插入
            if (tempFile.exists()) {
                tempFile.delete()
            }
            JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(tempFile))
            //保存
            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = enumeration.nextElement()
                String entryName = jarEntry.name
                ZipEntry zipEntry = new ZipEntry(entryName)
                InputStream inputStream = jarFile.getInputStream(zipEntry)
                if (isClassFile(entryName)) {
                    println("-------------------- handle jar file:<$entryName> --------------------")
                    jarOutputStream.putNextEntry(zipEntry)
                    ClassReader classReader = new ClassReader(IOUtils.toByteArray(inputStream))
                    ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                    ClassVisitor classVisitor = new ActivityClassVisitor(classWriter)
                    classReader.accept(classVisitor, org.objectweb.asm.ClassReader.EXPAND_FRAMES)
                    byte[] bytes = classWriter.toByteArray()
                    jarOutputStream.write(bytes)
                } else {
                    jarOutputStream.putNextEntry(zipEntry)
                    jarOutputStream.write(IOUtils.toByteArray(inputStream))
                }
                jarOutputStream.closeEntry()
            }
            jarOutputStream.close()
            jarFile.close()
            def dest = provider.getContentLocation(jarName + "_" + md5Name, jarInput.contentTypes, jarInput.scopes, Format.JAR)
            FileUtils.copyFile(tempFile, dest)
            tempFile.delete()
        }
    }

    static boolean isClassFile(String name){
        return (name.endsWith(".class") && !name.startsWith("R\$")
                && "R.class" != name && "BuildConfig.class" != name && name.contains("Activity"))
    }
}