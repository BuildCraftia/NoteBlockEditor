package com.itsazza.noteblockeditor

import com.itsazza.noteblockeditor.command.NoteBlockMenuCommand
import com.itsazza.noteblockeditor.listener.NoteBlockEventListener
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.Instrument
import org.bukkit.Material
import org.bukkit.plugin.java.JavaPlugin
import java.lang.IllegalArgumentException
import kotlin.collections.HashMap

class NoteBlockEditorPlugin : JavaPlugin() {
    companion object {
        lateinit var instance: NoteBlockEditorPlugin
            private set
    }

    val instruments = HashMap<Instrument, Material>()

    override fun onEnable() {
        instance = this
        getCommand("noteblock")?.setExecutor(NoteBlockMenuCommand)
        saveDefaultConfig()
        loadInstruments()
        Bukkit.getPluginManager().registerEvents(NoteBlockEventListener, this)
        Metrics(this, 11176)
    }

    fun loadInstruments() {
        instruments.clear()

        val configInstruments = config.getConfigurationSection("instruments")?.getKeys(false) ?: return
        for (instrumentString in configInstruments) {
            lateinit var material: Material
            lateinit var instrument: Instrument
            val materialString = config.getString("instruments.$instrumentString") ?: continue

            try {
              instrument = Instrument.valueOf(instrumentString)
            } catch (e: IllegalArgumentException) {
                continue
            }

            try {
                material = Material.valueOf(materialString)
            } catch (e: IllegalArgumentException) {
                logger.severe("Could not find material $materialString for instrument $instrumentString")
                continue
            }

            this.instruments[instrument] = material
        }
    }
}