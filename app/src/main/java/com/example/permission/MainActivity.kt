package com.example.permission

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.permission.databinding.ActivityMainBinding
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog

//참고 : https://codechacha.com/ko/android-request-runtime-permissions/
class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    val permission_list = arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textView.text = ""

        for (permission in permission_list) {

            val chk = checkCallingOrSelfPermission(permission)

            if(chk == PackageManager.PERMISSION_GRANTED) {
                binding.textView.append("$permission : 허용\n")
            } else if(chk == PackageManager.PERMISSION_DENIED) {
                binding.textView.append("$permission : 거부\n")
            }
        }

        binding.button.setOnClickListener {
            requestPermissions(permission_list, 0)
        }
    }

    //사용자에게 허용 여부를 확인 받으면 자동으로 호출되는 메소드
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        binding.textView.text = ""

        for(idx in grantResults.indices) {
            if(grantResults[idx] == PackageManager.PERMISSION_GRANTED) {
                binding.textView.append("${permissions[idx]} :허용 \ns")
            } else if(grantResults[idx] == PackageManager.PERMISSION_DENIED) {
                binding.textView.append("${permissions[idx]} :거부 \ns")
            }
        }

        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                //사용자가 거부 버튼을 클릭 했을때, 2번째까지는 다시 물어 본다.
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
            } else {
                showDialogToGetPermission()
            }
        }
    }

    private fun showDialogToGetPermission() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("권한 요청")
                .setMessage("파일 첨부를 위한 저장소 읽기 권한이 필요합니다.")
        builder.setPositiveButton("OK") { dialogInterface, i ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        builder.setNegativeButton("나중에") { dialogInterface, i ->
            // ignore
        }
        val dialog = builder.create()
        dialog.show()
    }
}