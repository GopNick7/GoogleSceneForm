package com.example.user.googlesceneform

import android.Manifest
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab.setOnClickListener { view ->

            TedPermission.with(this)
                    .setPermissionListener(object : PermissionListener {
                        override fun onPermissionGranted() {
                            copyFileOrDir("google-ar-asset-converter")
                        }

                        override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {

                        }
                    })
                    .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .check()

        }
        convert.setOnClickListener {
            converter()
        }
    }

    private fun converter() {
        File(getExternalFilesDir(null).toString() + "/google-ar-asset-converter/output/").mkdir()

        val root = getExternalFilesDir(null).toString() + "/google-ar-asset-converter"

//        execCommand(
//                "chmod", "u+x", "$root/sceneform_sdk/linux/converter",
//                "$root/sceneform_sdk/linux/converter", "-a", "-d", "--mat",
//                "$root/sceneform_sdk/default_materials/obj_material.sfm",
//                "--outdir",
//                "$root/output/",
//                "$root/input/andy.obj"
//        )

//        val process = Runtime.getRuntime().exec(arrayOf(
//                "chmod","u+x", "$root/sceneform_sdk/linux/converter"
//        ))
//        logProcess(process)

//        val process2 = Runtime.getRuntime().exec(arrayOf(
//                "chmod", "u+x",
//                "$root/sceneform_sdk/linux/converter", "-a", "-d", "--mat",
//                "$root/sceneform_sdk/default_materials/obj_material.sfm",
//                "--outdir",
//                "$root/output/",
//                "$root/input/andy.obj"
//        ))


        val a1 = "chmod u+x $root/sceneform_sdk/linux/converter"
        val a2 = "$root/sceneform_sdk/linux/converter -a -d --mat " +
                "$root/sceneform_sdk/default_materials/obj_material.sfm" +
                " " +
                "--outdir" +
                " " +
                "$root/output/" +
                " " +
                "$root/input/andy.obj"

        val process = Runtime.getRuntime().exec(a1)

        try {
            val os = DataOutputStream(process.outputStream)

            os.writeBytes(a2 + "\n")
            os.flush()
            os.close()

            process.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    fun copyFileOrDir(path: String) {
        val assetManager = this.assets
        var assets: Array<String>? = null
        try {
            assets = assetManager.list(path)
            if (assets!!.isEmpty()) {
                copyFile(path)
            } else {
                val fullPath = getExternalFilesDir(null).toString() + "/" + path
                val dir = File(fullPath)
                if (!dir.exists())
                    dir.mkdir()
                for (i in assets.indices) {
                    copyFileOrDir(path + "/" + assets[i])
                }
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
        }

    }

    private fun copyFile(filename: String) {
        val assetManager = this.assets
        try {
            val newFileName = getExternalFilesDir(null).toString() + "/" + filename
            var input: InputStream? = assetManager.open(filename)
            var out: OutputStream? = FileOutputStream(newFileName)

            val buffer = ByteArray(1024)
            var read: Int? = null
            while ({ read = input?.read(buffer); read }() != -1) {
                read?.let { out?.write(buffer, 0, it) }
            }
            input!!.close()
            input = null
            out?.flush()
            out?.close()
            out = null
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun execCommand(vararg str: String): Map<*, *> {
        val map = HashMap<Int, String>()
        val pb = ProcessBuilder(*str)
        pb.redirectErrorStream(true)
        var process: Process? = null
        try {
            process = pb.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        var reader: BufferedReader? = null
        if (process != null) {
            reader = BufferedReader(InputStreamReader(process.inputStream))
        }

        var line: String? = null
        val stringBuilder = StringBuilder()
        try {
            if (reader != null) {
                while ({ line = reader.readLine(); line }() != null) {
                    stringBuilder.append(line).append("\n")
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        try {
            process?.waitFor()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        if (process != null) {
            map[0] = process.exitValue().toString()
        }

        try {
            map[1] = stringBuilder.toString()
        } catch (e: StringIndexOutOfBoundsException) {
            if (stringBuilder.toString().isEmpty()) {
                return map
            }
        }
        return map
    }

}
