package com.itsazza.noteblockeditor.menu.notemenu

import com.itsazza.noteblockeditor.NoteBlockEditorPlugin
import com.itsazza.noteblockeditor.menu.buttons.closeButton
import com.itsazza.noteblockeditor.menu.instrumentmenu.NoteBlockInstrumentMenu
import com.itsazza.noteblockeditor.menu.noteBlockMenuTemplate
import de.themoep.inventorygui.GuiElement
import de.themoep.inventorygui.GuiElementGroup
import de.themoep.inventorygui.InventoryGui
import de.themoep.inventorygui.StaticGuiElement
import org.bukkit.Instrument
import org.bukkit.Material
import org.bukkit.Note
import org.bukkit.block.Block
import org.bukkit.block.data.type.NoteBlock
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

object NoteBlockNoteMenu {
    private val instance = NoteBlockEditorPlugin.instance

    fun openMenu(player: Player, block: Block, note: Note, instrument: Instrument) {
        createMenu(player, block, note, instrument).show(player)
    }

    private fun createMenu(player: Player, block: Block, note: Note, instrument: Instrument) : InventoryGui {
        val gui = InventoryGui(
            instance,
            instance.getLangString("menu-title"),
            noteBlockMenuTemplate
        )

        val group = GuiElementGroup('n')

        for(item in listOfNotes) {
            group.addElement(createNoteBlockButton(block, item, instrument, note))
        }

        gui.addElement(group)
        if(player.hasPermission("noteblockeditor.instruments")) {
            gui.addElement(
                StaticGuiElement(
                    'i',
                    ItemStack(Material.REDSTONE),
                    1,
                    {
                        NoteBlockInstrumentMenu.openMenu(player, block, note, instrument)
                        return@StaticGuiElement true
                    },
                    *instance.getLangString("button-instrument-menu").split('\n').toTypedArray()
                )
            )
        }
        gui.closeAction = InventoryGui.CloseAction { false }
        gui.addElement(closeButton)
        return gui
    }

    private fun createNoteBlockButton(
        block: Block,
        note: NoteBlockNote,
        instrument: Instrument,
        currentNote: Note
    ): StaticGuiElement {
        val item = ItemStack(Material.NOTE_BLOCK)
        val itemMeta = item.itemMeta!!
        if (Note(note.value - 1) == currentNote) {
            itemMeta.addEnchant(Enchantment.LOOTING, 1, true)
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            item.itemMeta = itemMeta
        }

        return StaticGuiElement(
            'b',
            item,
            note.value,
            GuiElement.Action {
                val newNote = Note(note.value - 1)
                val player = it.whoClicked as Player

                when {
                    it.type.isRightClick -> {
                        player.playNote(player.location, instrument, newNote)
                        return@Action true
                    }
                    it.type.isLeftClick -> {
                        player.playNote(player.location, instrument, newNote)
                        val data = block.blockData as NoteBlock
                        data.note = newNote
                        block.blockData = data
                        player.sendMessage(instance.getLangString("menu-note-change-message").format(note.name))
                        it.gui.destroy()
                        return@Action true
                    }
                    else -> {
                        return@Action true
                    }
                }
            },
            instance.getLangString("menu-note-change-title").format(note.name),
            *instance.getLangString("menu-note-change-description").format(note.name, note.value - 1).split('\n').toTypedArray()
        )
    }
}