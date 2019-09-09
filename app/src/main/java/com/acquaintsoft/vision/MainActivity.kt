/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.acquaintsoft.vision

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.TextView

import com.google.android.gms.common.api.CommonStatusCodes

/**
 * Main activity demonstrating how to pass extra parameters to an activity that
 * recognizes text.
 */
class MainActivity : Activity(), View.OnClickListener {
    // Use a compound button so either checkbox or switch widgets work.
    private var autoFocus: CompoundButton? = null
    private var useFlash: CompoundButton? = null
    private var statusMessage: TextView? = null
    private var textValue: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusMessage = findViewById<View>(R.id.status_message) as TextView
        textValue = findViewById<View>(R.id.text_value) as TextView

        autoFocus = findViewById<View>(R.id.auto_focus) as CompoundButton
        useFlash = findViewById<View>(R.id.use_flash) as CompoundButton

        findViewById<View>(R.id.read_text).setOnClickListener(this)
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    override fun onClick(v: View) {
        if (v.id == R.id.read_text) {
            // launch Ocr capture activity.
            val intent = Intent(this, OcrCaptureActivity::class.java)
            intent.putExtra(OcrCaptureActivity.AutoFocus, autoFocus!!.isChecked)
            intent.putExtra(OcrCaptureActivity.UseFlash, useFlash!!.isChecked)

            startActivityForResult(intent, RC_OCR_CAPTURE)
        }
    }

    /**
     * Called when an activity you launched exits, giving you the requestCode
     * you started it with, the resultCode it returned, and any additional
     * data from it.  The <var>resultCode</var> will be
     * [.RESULT_CANCELED] if the activity explicitly returned that,
     * didn't return any result, or crashed during its operation.
     *
     *
     *
     * You will receive this call immediately before onResume() when your
     * activity is re-starting.
     *
     *
     *
     * @param requestCode The integer request code originally supplied to
     * startActivityForResult(), allowing you to identify who this
     * result came from.
     * @param resultCode  The integer result code returned by the child activity
     * through its setResult().
     * @param data        An Intent, which can return result data to the caller
     * (various data can be attached to Intent "extras").
     * @see .startActivityForResult
     *
     * @see .createPendingResult
     *
     * @see .setResult
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_OCR_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    val text = data.getStringExtra(OcrCaptureActivity.TextBlockObject)
                    statusMessage!!.setText(R.string.ocr_success)
                    textValue!!.text = text
                    Log.d(TAG, "Text read: $text")
                } else {
                    statusMessage!!.setText(R.string.ocr_failure)
                    Log.d(TAG, "No Text captured, intent data is null")
                }
            } else {
                statusMessage!!.text = String.format(getString(R.string.ocr_error),
                        CommonStatusCodes.getStatusCodeString(resultCode))
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {

        private val RC_OCR_CAPTURE = 9003
        private val TAG = "MainActivity"
    }
}
