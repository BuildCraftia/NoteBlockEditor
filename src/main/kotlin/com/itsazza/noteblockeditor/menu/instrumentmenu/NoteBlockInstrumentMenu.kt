package com.itsazza.noteblockeditor.menu.instrumentmenu

import com.itsazza.noteblockeditor.NoteBlockEditorPlugin
import com.itsazza.noteblockeditor.menu.buttons.closeButton
import com.itsazza.noteblockeditor.menu.noteBlockMenuTemplate
import com.itsazza.noteblockeditor.menu.notemenu.NoteBlockNoteMenu
import com.itsazza.noteblockeditor.util.canPlace
import com.itsazza.noteblocksplus.api.NoteBlocksPlusAPI
import de.themoep.inventorygui.GuiElementGroup
import de.themoep.inventorygui.InventoryGui
import de.themoep.inventorygui.StaticGuiElement
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@Suppress("DEPRECATION")
object NoteBlockInstrumentMenu {
    private val instance = NoteBlockEditorPlugin.instance

    fun openMenu(player: Player, block: Block, currentNote: Note, currentInstrument: Instrument) {
        createMenu(player, block, currentNote, currentInstrument).show(player)
    }

    private fun createMenu(player: Player, block: Block, currentNote: Note, currentInstrument: Instrument) : InventoryGui {
        val gui = InventoryGui(
            instance,
            instance.getLangString("menu-title"),
            noteBlockMenuTemplate
        )

        val group = GuiElementGroup('n')

        val instruments = NoteBlockEditorPlugin.instance.instruments
        for (instrument in instruments) {
            group.addElement(createInstrumentButton(block, currentNote, instrument.key, instrument.value))
        }

        if (NoteBlockEditorPlugin.noteBlocksPlusEnabled) {
            NoteBlocksPlusAPI.getSounds().forEach {
                group.addElement(createNoteBlocksPlusInstrumentButton(block, currentNote, it.value, it.key))
            }
        }

        gui.addElement(group)
        gui.addElement(StaticGuiElement('r',
            ItemStack(Material.ARROW),
            1,
            {   NoteBlockNoteMenu.openMenu(player, block, currentNote, currentInstrument)
                return@StaticGuiElement true},
            instance.getLangString("button-back")
        ))
        gui.addElement(closeButton)
        gui.closeAction = InventoryGui.CloseAction { false }
        return gui
    }

    private fun createInstrumentButton(block: Block, note: Note, newInstrument: Instrument, icon: Material) : StaticGuiElement {
        val item = ItemStack(icon)
        val instrumentName = newInstrument.name.split("_").joinToString(" ") { it.toLowerCase().capitalize() }

        return StaticGuiElement('i',
            item,
            {
                val player = it.event.whoClicked as Player
                when {
                    it.event.isLeftClick -> {
                        setBlockBelowNoteBlock(player, block, icon).also { response ->
                            if (!response) {
                                return@StaticGuiElement true
                            }
                        }
                        it.gui.destroy()
                        return@StaticGuiElement true
                    }
                    it.event.isRightClick -> {
                        player.playNote(player.location, newInstrument, note)
                        return@StaticGuiElement  true
                    }
                    else -> {
                        return@StaticGuiElement true
                    }
                }
            },
            instance.getLangString("menu-instrument-change-title").format(instrumentName),
            *instance.getLangString("menu-instrument-change-description").format(instrumentName).split('\n').toTypedArray()
        )
    }

    private fun createNoteBlocksPlusInstrumentButton(block: Block, note: Note, sound: String, icon: Material): StaticGuiElement {
        val name = sound.replace("^[^:]+:".toRegex(), "")
        val soundString = name.split(".", "_").joinToString(" ") { it.toLowerCase().capitalize() }

        return StaticGuiElement(
            'i',
            ItemStack(icon),
            {
                val player = it.event.whoClicked as Player
                when {
                    it.event.isLeftClick -> {
                        setBlockBelowNoteBlock(player, block, icon).also { response ->
                            if (!response) return@StaticGuiElement true
                        }
                        it.gui.destroy()
                        return@StaticGuiElement true
                    }
                    it.event.isRightClick -> {
                        val pitch = NoteBlocksPlusAPI.getNotePitch(note.id.toFloat())
                        player.playSound(player.location, sound, 1f, pitch)
                        return@StaticGuiElement true
                    }
                    else -> return@StaticGuiElement true
                }
            },
            instance.getLangString("menu-instrument-change-title").format(soundString),
            *instance.getLangString("menu-instrument-change-description").format(soundString).split('\n').toTypedArray()
        )
    }

    private fun setBlockBelowNoteBlock(player: Player, block: Block, material: Material) : Boolean {
        val blockBelow = block.getRelative(BlockFace.DOWN)
        if (!player.canPlace(blockBelow)) {
            player.sendMessage(instance.getLangString("no-build-permission-below"))
            player.playSound(player.location, Sound.ENTITY_VILLAGER_NO, 1f, 1f)
            return false
        }
        blockBelow.setType(material, true)
        return true
    }
}