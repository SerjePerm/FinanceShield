package com.goodsoft.financeshield.fragments

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.goodsoft.financeshield.MainActivity
import com.goodsoft.financeshield.R
import com.goodsoft.financeshield.activities.NotificationsListActivity
import com.goodsoft.financeshield.activities.ParsersListActivity
import com.goodsoft.financeshield.databinding.FragmentSettingsBinding
import com.goodsoft.financeshield.utils.dateToTimeStr
import java.util.Calendar

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var settings: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        activity?.setTitle(R.string.tab_settings_title)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeButtonsClicks()
        settings = requireContext().getSharedPreferences(R.string.app_name.toString(), 0)
        initializeSettingsLoad()
    }

    private fun initializeButtonsClicks() {
        binding.settingsBtnStartPush.setOnClickListener { startActivity(Intent(activity, ParsersListActivity::class.java)) }
        binding.settingsBtnStartnotifications.setOnClickListener { startActivity(Intent(activity, NotificationsListActivity::class.java)) }
        binding.settingsBtnCheckService.setOnClickListener { checkNLSComplex() }
        binding.settingsBtnOpenSettings.setOnClickListener { startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")) }
        binding.settingsBtnCreateP.setOnClickListener { randomMSG() }
        binding.settingsCbPushAnalyze.setOnClickListener { switchAutoPushAnalyze() }
    }

    private fun initializeSettingsLoad() {
        binding.settingsCbPushAnalyze.isChecked = settings.getBoolean("PUSH auto analyze", true)
    }

    private fun checkNLSComplex() {
        val msg1 = if ((activity as MainActivity).checkNLSAccess()) "Разрешение на чтение Push-уведомлений получено" else "!!! Разрешение на чтение Push-уведомлений не получено"
        val msg2 = if ((activity as MainActivity).checkNLSStatus()) "Сервис автоматического распознания работает" else "!!! Сервис автоматического распознания не работает"
        val myAlertDialog = AlertDialog.Builder(activity)
        myAlertDialog.setTitle(R.string.SF_push_service_start)
        myAlertDialog.setMessage(msg1 + "\n"+ "\n" + msg2)
        myAlertDialog.setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.cancel() }
        myAlertDialog.show()
    }

    private fun switchAutoPushAnalyze() {
            if (settings.getBoolean("PUSH auto analyze", true)) {
                settings.edit().putBoolean("PUSH auto analyze", false).apply()
                Toast.makeText(requireContext(), R.string.SF_push_switch_off, Toast.LENGTH_SHORT).show()
            } else {
                settings.edit().putBoolean("PUSH auto analyze", true).apply()
                Toast.makeText(requireContext(), R.string.SF_push_switch_on, Toast.LENGTH_SHORT).show()
            }
    }

    private fun randomMSG() {
        val txt = "Покупка на 2 189,56 ₽, карта *4519\nДоступно 12 742."
        (activity as MainActivity).createNotification("Pyaterochka 1", txt + (10..99).random().toString())
        //(activity as MainActivity).createNotification("title " + (0..10).random().toString(), "test карта text " + (100..200).random().toString())
    }
}