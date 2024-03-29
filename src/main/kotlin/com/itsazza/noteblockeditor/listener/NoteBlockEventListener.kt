package com.itsazza.noteblockeditor.listener

import com.itsazza.noteblockeditor.NoteBlockEditorPlugin
import com.itsazza.noteblockeditor.menu.notemenu.NoteBlockNoteMenu
import com.itsazza.noteblockeditor.util.canPlace
import org.bukkit.Material
import org.bukkit.block.data.type.NoteBlock
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

object NoteBlockEventListener: Listener {
    @EventHandler
    fun onNoteBlockClick(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_BLOCK) return

        val player = event.player
        if (!player.isSneaking) return
        if (player.inventory.itemInMainHand.type != Material.AIR) return

        val clickedBlock = event.clickedBlock ?: return
        if (clickedBlock.blockData !is NoteBlock) return

        if(!player.hasPermission("noteblockeditor.interact")) return
        val noteBlock = clickedBlock.blockData as NoteBlock

        if(!player.canPlace(clickedBlock) && !player.hasPermission("noteblockeditor.bypass")) {
            player.sendMessage(NoteBlockEditorPlugin.instance.getLangString("no-build-permission"))
            return
        }

        NoteBlockNoteMenu.openMenu(player, clickedBlock, noteBlock.note, noteBlock.instrument)
        event.isCancelled = true
    }
}