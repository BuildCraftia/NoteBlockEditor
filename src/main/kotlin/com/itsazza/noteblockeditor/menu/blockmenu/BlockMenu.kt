package com.itsazza.noteblockeditor.menu.blockmenu

import com.itsazza.noteblockeditor.NoteBlockEditorPlugin
import com.itsazza.noteblockeditor.menu.buttons.closeButton
import com.itsazza.noteblockeditor.menu.notemenu.NoteBlockNote
import com.itsazza.noteblockeditor.menu.notemenu.listOfNotes
import com.itsazza.noteblockeditor.util.Noteblock
import de.themoep.inventorygui.GuiElementGroup
import de.themoep.inventorygui.InventoryGui
import de.themoep.inventorygui.StaticGuiElement
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object BlockMenu {
    private val instance = NoteBlockEditorPlugin.instance

    fun open(player: Player) {
        create().show(player)
    }

    private fun create(): InventoryGui {
        val gui = InventoryGui(
            instance,
            instance.getLangString("menu-title"),
            arrayOf(
                "         ",
                " 0000000 ",
                " 0000000 ",
                " 0000000 ",
                " 0000000 ",
                "    c    ",
            )
        )

        val group = GuiElementGroup('0')
        for (note in listOfNotes) {
            group.addElement(createGiveNoteBlockButton(note))
        }

        gui.addElements(group, closeButton)
        gui.setCloseAction { false }
        return gui
    }

    private fun createGiveNoteBlockButton(note: NoteBlockNote): StaticGuiElement {
        return StaticGuiElement(
            '0',
            ItemStack(Material.NOTE_BLOCK, note.value),
            {
                val player = it.whoClicked as Player
                Noteblock.giveBlock(player, note.value)
                player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BIT, 1f, 1f)
                return@StaticGuiElement true
            },
            instance.getLangString("menu-give-description").format(note.value)
        )
    }
}