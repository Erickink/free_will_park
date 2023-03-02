package com.example.freewillpark

import androidx.lifecycle.ViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.gms.maps.model.LatLng

class SettingSave: ViewModel() {
    private var typeBoolean: Boolean = false
    private var veloBoolean: Boolean = false
    private var trotBoolean: Boolean = false
    private var adminBoolean: Boolean = false
    private var nbPoints = 0
    private lateinit var buttonPrincipal: FloatingActionButton
    private lateinit var lastMarkedLL: LatLng

    fun setTypeBoolean(typeB : Boolean)
    {
        typeBoolean = typeB
    }

    fun getTypeBoolean(): Boolean
    {
        return typeBoolean
    }

    fun setVeloBoolean(veloB : Boolean)
    {
        veloBoolean = veloB
    }

    fun getVeloBoolean(): Boolean
    {
        return veloBoolean
    }

    fun setTrotBoolean(trotB : Boolean)
    {
        trotBoolean = trotB
    }

    fun getTrotBoolean(): Boolean
    {
        return trotBoolean
    }

    fun setAdminBoolean(adminB: Boolean)
    {
        adminBoolean = adminB
    }

    fun getAdminBoolean(): Boolean
    {
        return adminBoolean
    }

    fun setnbPoints(nbPointsB: Int)
    {
        nbPoints = nbPointsB
    }

    fun getnbPoints(): Int
    {
        return nbPoints
    }

    fun setbuttonPrincipale(buttonP: FloatingActionButton)
    {
        buttonPrincipal = buttonP
    }

    fun getbuttonPrincipale(): FloatingActionButton
    {
        return buttonPrincipal
    }

    fun setlastMarkedLL(lastMarkedLLP: LatLng)
    {
        lastMarkedLL = lastMarkedLLP
    }

    fun getlastMarkedLL(): LatLng
    {
        return lastMarkedLL
    }
}