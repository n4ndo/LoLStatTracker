package com.rajohnson;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import com.rajohnson.Match;

@SuppressWarnings("serial")
public class MatchCellRenderer<E> extends JPanel implements ListCellRenderer<E> {

	private static HashMap<String, String> _champIcons;
	private static HashMap<Integer, String> _itemIcons;
	
	public static enum CellSize
	{
		SMALL, MEDIUM, LARGE;
	}
	static {
        _champIcons = new HashMap<String,String>();
        _champIcons.put(  "none", "nope.png" ); // represents a catch-all champion for stats
        _champIcons.put(  "Annie", "res/champIcon/Annie.png");
        _champIcons.put(  "Olaf", "res/champIcon/Olaf.png" );
        _champIcons.put(  "Galio", "res/champIcon/Galio.png");
        _champIcons.put(  "Twisted Fate", "res/champIcon/TwistedFate.png");
        _champIcons.put(  "Xin Zhao", "res/champIcon/XinZhao.png");
        _champIcons.put(  "Urgot", "res/champIcon/Urgot.png");
        _champIcons.put(  "LeBlanc", "res/champIcon/LeBlanc.png");
        _champIcons.put(  "Vladimir", "res/champIcon/Vladimir.png");
        _champIcons.put(  "Fiddlesticks", "res/champIcon/FiddleSticks.png");
        _champIcons.put( "Kayle", "res/champIcon/Kayle.png");
        _champIcons.put( "Master Yi", "res/champIcon/MasterYi.png");
        _champIcons.put( "Alistar", "res/champIcon/Alistar.png");
        _champIcons.put( "Ryze", "res/champIcon/Ryze.png");
        _champIcons.put( "Sion", "res/champIcon/Sion.png");
        _champIcons.put( "Sivir", "res/champIcon/Sivir.png");
        _champIcons.put( "Soraka", "res/champIcon/Soraka.png");
        _champIcons.put( "Teemo", "res/champIcon/Teemo.png");
        _champIcons.put( "Tristana", "res/champIcon/Tristana.png");
        _champIcons.put( "Warwick", "res/champIcon/Warwick.png");
        _champIcons.put( "Nunu", "res/champIcon/Nunu.png");
        _champIcons.put( "Miss Fortune", "res/champIcon/MissFortune.png");
        _champIcons.put( "Ashe", "res/champIcon/Ashe.png");
        _champIcons.put( "Tryndamere", "res/champIcon/Tryndamere.png");
        _champIcons.put( "Jax", "res/champIcon/Jax.png");
        _champIcons.put( "Morgana", "res/champIcon/Morgana.png");
        _champIcons.put( "Zilean", "res/champIcon/Zilean.png");
        _champIcons.put( "Singed", "res/champIcon/Singed.png");
        _champIcons.put( "Evelynn", "res/champIcon/Evelynn.png");
        _champIcons.put( "Twitch", "res/champIcon/Twitch.png");
        _champIcons.put( "Karthus", "res/champIcon/Karthus.png");
        _champIcons.put( "Cho'Gath", "res/champIcon/Chogath.png");
        _champIcons.put( "Amumu", "res/champIcon/Amumu.png");
        _champIcons.put( "Rammus", "res/champIcon/Rammus.png");
        _champIcons.put( "Anivia", "res/champIcon/Anivia.png");
        _champIcons.put( "Shaco", "res/champIcon/Shaco.png");
        _champIcons.put( "Dr. Mundo", "res/champIcon/DrMundo.png");
        _champIcons.put( "Sona", "res/champIcon/Sona.png");
        _champIcons.put( "Kassadin", "res/champIcon/Kassadin.png");
        _champIcons.put( "Irelia", "res/champIcon/Irelia.png");
        _champIcons.put( "Janna", "res/champIcon/Janna.png");
        _champIcons.put( "Gangplank", "res/champIcon/Gangplank.png");
        _champIcons.put( "Corki", "res/champIcon/Corki.png");
        _champIcons.put( "Karma", "res/champIcon/Karma.png");
        _champIcons.put( "Taric", "res/champIcon/Taric.png");
        _champIcons.put( "Veigar", "res/champIcon/Veigar.png");
        _champIcons.put( "Trundle", "res/champIcon/Trundle.png");
        _champIcons.put( "Swain", "res/champIcon/Swain.png");
        _champIcons.put( "Caitlyn", "res/champIcon/Caitlyn.png");
        _champIcons.put( "Blitzcrank", "res/champIcon/Blitzcrank.png");
        _champIcons.put( "Malphite", "res/champIcon/Malphite.png");
        _champIcons.put( "Katarina", "res/champIcon/Katarina.png");
        _champIcons.put( "Nocturne", "res/champIcon/Nocturne.png");
        _champIcons.put( "Maokai", "res/champIcon/Maokai.png");
        _champIcons.put( "Renekton", "res/champIcon/Renekton.png");
        _champIcons.put( "Jarvan IV", "res/champIcon/JarvanIV.png");
        _champIcons.put( "Orianna", "res/champIcon/Orianna.png");
        _champIcons.put( "Wukong", "res/champIcon/MonkeyKing.png");
        _champIcons.put( "Brand", "res/champIcon/Brand.png");
        _champIcons.put( "Lee Sin", "res/champIcon/LeeSin.png");
        _champIcons.put( "Vayne", "res/champIcon/Vayne.png");
        _champIcons.put( "Rumble", "res/champIcon/Rumble.png");
        _champIcons.put( "Cassiopeia", "res/champIcon/Cassiopeia.png");
        _champIcons.put( "Skarner", "res/champIcon/Skarner.png");
        _champIcons.put( "Heimerdinger", "res/champIcon/Heimerdinger.png");
        _champIcons.put( "Nasus", "res/champIcon/Nasus.png");
        _champIcons.put( "Nidalee", "res/champIcon/Nidalee.png");
        _champIcons.put( "Udyr", "res/champIcon/Udyr.png");
        _champIcons.put( "Poppy", "res/champIcon/Poppy.png");
        _champIcons.put( "Gragas", "res/champIcon/Gragas.png");
        _champIcons.put( "Pantheon", "res/champIcon/Pantheon.png");
        _champIcons.put( "Ezreal", "res/champIcon/Ezreal.png");
        _champIcons.put( "Mordekaiser", "res/champIcon/Mordekaiser.png");
        _champIcons.put( "Yorick", "res/champIcon/Yorick.png");
        _champIcons.put( "Akali", "res/champIcon/Akali.png");
        _champIcons.put( "Kennen", "res/champIcon/Kennen.png");
        _champIcons.put( "Garen", "res/champIcon/Garen.png");
        _champIcons.put( "Leona", "res/champIcon/Leona.png");
        _champIcons.put( "Malzahar", "res/champIcon/Malzahar.png");
        _champIcons.put( "Talon", "res/champIcon/Talon.png");
        _champIcons.put( "Riven", "res/champIcon/Riven.png");
        _champIcons.put( "Kog'Maw", "res/champIcon/KogMaw.png");
        _champIcons.put( "Shen", "res/champIcon/Shen.png");
        _champIcons.put( "Lux", "res/champIcon/Lux.png");
        _champIcons.put("Xerath", "res/champIcon/Xerath.png");
        _champIcons.put("Shyvana", "res/champIcon/Shyvana.png");
        _champIcons.put("Ahri", "res/champIcon/Ahri.png");
        _champIcons.put("Graves", "res/champIcon/Graves.png");
        _champIcons.put("Fizz", "res/champIcon/Fizz.png");
        _champIcons.put("Volibear", "res/champIcon/Volibear.png");
        _champIcons.put("Varus", "res/champIcon/Varus.png");
        _champIcons.put("Nautilus", "res/champIcon/Nautilus.png");
        _champIcons.put("Viktor", "res/champIcon/Viktor.png");
        _champIcons.put("Sejuani", "res/champIcon/Sejuani.png");
        _champIcons.put("Fiora", "res/champIcon/Fiora.png");
        _champIcons.put("Ziggs", "res/champIcon/Ziggs.png");
        _champIcons.put("Lulu", "res/champIcon/Lulu.png");
        _champIcons.put("Draven", "res/champIcon/Draven.png");
        _champIcons.put("Hecarim", "res/champIcon/Hecarim.png");
        _champIcons.put("Darius", "res/champIcon/Darius.png");
        _champIcons.put("Jayce", "res/champIcon/Jayce.png");
        _champIcons.put("Zyra", "res/champIcon/Zyra.png");
        _champIcons.put("Diana", "res/champIcon/Diana.png");
        _champIcons.put("Rengar", "res/champIcon/Rengar.png");
        _champIcons.put("Syndra", "res/champIcon/Syndra.png");
        _champIcons.put("Kha'Zix", "res/champIcon/Khazix.png");
        _champIcons.put( "Elise", "res/champIcon/Elise.png");
        _champIcons.put("Zed", "res/champIcon/Zed.png");
        _champIcons.put("Nami", "res/champIcon/Nami.png");
        _champIcons.put("Vi", "res/champIcon/Vi.png");
        _champIcons.put("Thresh", "res/champIcon/Thresh.png");
        _champIcons.put("Quinn", "res/champIcon/Quinn.png");
        _champIcons.put("Zac", "res/champIcon/Zac.png");
        _champIcons.put("Lissandra", "res/champIcon/Lissandra.png");
        _champIcons.put("Aatrox", "res/champIcon/Aatrox.png");
        _champIcons.put("Lucian", "res/champIcon/Lucian.png");
        _champIcons.put("Jinx", "res/champIcon/Jinx.png");
        _champIcons.put("Yasuo", "res/champIcon/Yasuo.png");
        
        
        _itemIcons = new HashMap<Integer, String>();
        _itemIcons.put(0, "res/itemIcon/EmptyIcon_test.png");
        _itemIcons.put(-1, "res/itemIcon/EmptyIcon.png");
        _itemIcons.put(3191, "res/itemIcon/Seeker's_Armguard.png");
        _itemIcons.put(3401, "res/itemIcon/Face_of_the_Mountain.png");
        _itemIcons.put(3097, "res/itemIcon/Targon's_Brace.png");
        _itemIcons.put(3302, "res/itemIcon/Relic_Shield.png");
        _itemIcons.put(3284,"res/itemIcon/Boots_of_Swiftness_-_Enchantment_Alacrity.png");
        _itemIcons.put(3280,"res/itemIcon/Boots_of_Swiftness_-_Enchantment_Homeguard.png");
        _itemIcons.put(3281,"res/itemIcon/Boots_of_Swiftness_-_Enchantment_Captain.png");
        _itemIcons.put(3282,"res/itemIcon/Boots_of_Swiftness_-_Enchantment_Furor.png");
        _itemIcons.put(3283,"res/itemIcon/Boots_of_Swiftness_-_Enchantment_Distortion.png");
        _itemIcons.put(3269,"res/itemIcon/Mercury's_Treads_-_Enchantment_Alacrity.png");
        _itemIcons.put(3268,"res/itemIcon/Mercury's_Treads_-_Enchantment_Distortion.png");
        _itemIcons.put(3271,"res/itemIcon/Boots_of_Mobility_-_Enchantment_Captain.png");
        _itemIcons.put(3270,"res/itemIcon/Boots_of_Mobility_-_Enchantment_Homeguard.png");
        _itemIcons.put(3265,"res/itemIcon/Mercury's_Treads_-_Enchantment_Homeguard.png");
        _itemIcons.put(3264,"res/itemIcon/Ninja_Tabi_-_Enchantment_Alacrity.png");
        _itemIcons.put(3267,"res/itemIcon/Mercury's_Treads_-_Enchantment_Furor.png");
        _itemIcons.put(3266,"res/itemIcon/Mercury's_Treads_-_Enchantment_Captain.png");
        _itemIcons.put(3277,"res/itemIcon/Ionian_Boots_of_Lucidity_-_Enchantment_Furor.png");
        _itemIcons.put(3276,"res/itemIcon/Ionian_Boots_of_Lucidity_-_Enchantment_Captain.png");
        _itemIcons.put(3279,"res/itemIcon/Ionian_Boots_of_Lucidity_-_Enchantment_Alacrity.png");
        _itemIcons.put(3278,"res/itemIcon/Ionian_Boots_of_Lucidity_-_Enchantment_Distortion.png");
        _itemIcons.put(3273,"res/itemIcon/Boots_of_Mobility_-_Enchantment_Distortion.png");
        _itemIcons.put(3272,"res/itemIcon/Boots_of_Mobility_-_Enchantment_Furor.png");
        _itemIcons.put(3275,"res/itemIcon/Ionian_Boots_of_Lucidity_-_Enchantment_Homeguard.png");
        _itemIcons.put(3274,"res/itemIcon/Boots_of_Mobility_-_Enchantment_Alacrity.png");
        _itemIcons.put(1033,"res/itemIcon/Null-Magic_Mantle.png");
        _itemIcons.put(1036,"res/itemIcon/Long_Sword.png");
        _itemIcons.put(1037,"res/itemIcon/Pickaxe.png");
        _itemIcons.put(1038,"res/itemIcon/B.F._Sword.png");
        _itemIcons.put(3222,"res/itemIcon/Mikael's_Crucible.png");
        _itemIcons.put(1039,"res/itemIcon/Hunter's_Machete.png");
        _itemIcons.put(1026,"res/itemIcon/Blasting_Wand.png");
        _itemIcons.put(1027,"res/itemIcon/Sapphire_Crystal.png");
        _itemIcons.put(1028,"res/itemIcon/Ruby_Crystal.png");
        _itemIcons.put(1029,"res/itemIcon/Cloth_Armor.png");
        _itemIcons.put(1031,"res/itemIcon/Chain_Vest.png");
        _itemIcons.put(3200,"res/itemIcon/The_Hex_Core.png");
        _itemIcons.put(1051,"res/itemIcon/Brawler's_Gloves.png");
        _itemIcons.put(1053,"res/itemIcon/Vampiric_Scepter.png");
        _itemIcons.put(1052,"res/itemIcon/Amplifying_Tome.png");
        _itemIcons.put(1055,"res/itemIcon/Doran's_Blade.png");
        _itemIcons.put(3207,"res/itemIcon/Spirit_of_the_Ancient_Golem.png");
        _itemIcons.put(1054,"res/itemIcon/Doran's_Shield.png");
        _itemIcons.put(3206,"res/itemIcon/Spirit_of_the_Spectral_Wraith.png");
        _itemIcons.put(3209,"res/itemIcon/Spirit_of_the_Elder_Lizard.png");
        _itemIcons.put(1043,"res/itemIcon/Recurve_Bow.png");
        _itemIcons.put(1042,"res/itemIcon/Dagger.png");
        _itemIcons.put(3250,"res/itemIcon/Berserker's_Greaves_-_Enchantment_Homeguard.png");
        _itemIcons.put(3251,"res/itemIcon/Berserker's_Greaves_-_Enchantment_Captain.png");
        _itemIcons.put(3254,"res/itemIcon/Berserker's_Greaves_-_Enchantment_Alacrity.png");
        _itemIcons.put(3255,"res/itemIcon/Sorcerer's_Shoes_-_Enchantment_Homeguard.png");
        _itemIcons.put(3252,"res/itemIcon/Berserker's_Greaves_-_Enchantment_Furor.png");
        _itemIcons.put(3253,"res/itemIcon/Berserker's_Greaves_-_Enchantment_Distortion.png");
        _itemIcons.put(3258,"res/itemIcon/Sorcerer's_Shoes_-_Enchantment_Distortion.png");
        _itemIcons.put(1058,"res/itemIcon/Needlessly_Large_Rod.png");
        _itemIcons.put(3259,"res/itemIcon/Sorcerer's_Shoes_-_Enchantment_Alacrity.png");
        _itemIcons.put(1056,"res/itemIcon/Doran's_Ring.png");
        _itemIcons.put(3256,"res/itemIcon/Sorcerer's_Shoes_-_Enchantment_Captain.png");
        _itemIcons.put(1057,"res/itemIcon/Negatron_Cloak.png");
        _itemIcons.put(3257,"res/itemIcon/Sorcerer's_Shoes_-_Enchantment_Furor.png");
        _itemIcons.put(1062,"res/itemIcon/Prospector's_Blade.png");
        _itemIcons.put(3262,"res/itemIcon/Ninja_Tabi_-_Enchantment_Furor.png");
        _itemIcons.put(3263,"res/itemIcon/Ninja_Tabi_-_Enchantment_Distortion.png");
        _itemIcons.put(1063,"res/itemIcon/Prospector's_Ring.png");
        _itemIcons.put(3260,"res/itemIcon/Ninja_Tabi_-_Enchantment_Homeguard.png");
        _itemIcons.put(3261,"res/itemIcon/Ninja_Tabi_-_Enchantment_Captain.png");
        _itemIcons.put(1080,"res/itemIcon/Spirit_Stone.png");
        _itemIcons.put(3165,"res/itemIcon/Morellonomicon.png");
        _itemIcons.put(3167,"res/itemIcon/3167_Bone_Tooth.png");
        _itemIcons.put(3166,"res/itemIcon/3166_Bone_Tooth.png");
        _itemIcons.put(3157,"res/itemIcon/Zhonya's_Hourglass.png");
        _itemIcons.put(3156,"res/itemIcon/Maw_of_Malmortius.png");
        _itemIcons.put(3159,"res/itemIcon/Grez's_Spectral_Lantern.png");
        _itemIcons.put(3158,"res/itemIcon/Ionian_Boots_of_Lucidity.png");
        _itemIcons.put(3153,"res/itemIcon/Blade_of_the_Ruined_King.png");
        _itemIcons.put(3152,"res/itemIcon/Will_of_the_Ancients.png");
        _itemIcons.put(3155,"res/itemIcon/Hexdrinker.png");
        _itemIcons.put(3154,"res/itemIcon/Wriggle's_Lantern.png");
        _itemIcons.put(2048,"res/itemIcon/Ichor_of_Illumination.png");
        _itemIcons.put(2049,"res/itemIcon/Sightstone.png");
        _itemIcons.put(2050,"res/itemIcon/Explorer's_Ward.png");
        _itemIcons.put(3151,"res/itemIcon/Liandry's_Torment.png");
        _itemIcons.put(3144,"res/itemIcon/Bilgewater_Cutlass.png");
        _itemIcons.put(3145,"res/itemIcon/Hextech_Revolver.png");
        _itemIcons.put(3146,"res/itemIcon/Hextech_Gunblade.png");
        _itemIcons.put(3140,"res/itemIcon/Quicksilver_Sash.png");
        _itemIcons.put(3141,"res/itemIcon/Sword_of_the_Occult.png");
        _itemIcons.put(3142,"res/itemIcon/Youmuu's_Ghostblade.png");
        _itemIcons.put(3143,"res/itemIcon/Randuin's_Omen.png");
        _itemIcons.put(3136,"res/itemIcon/Haunting_Guise.png");
        _itemIcons.put(3139,"res/itemIcon/Mercurial_Scimitar.png");
        _itemIcons.put(3198,"res/itemIcon/Augment:_Death.png");
        _itemIcons.put(3197,"res/itemIcon/Augment:_Gravity.png");
        _itemIcons.put(3196,"res/itemIcon/Augment:_Power.png");
        _itemIcons.put(3190,"res/itemIcon/Locket_of_the_Iron_Solari.png");
        _itemIcons.put(3188,"res/itemIcon/Blackfire_Torch.png");
        _itemIcons.put(3187,"res/itemIcon/Hextech_Sweeper.png");
        _itemIcons.put(3186,"res/itemIcon/Kitae's_Bloodrazor.png");
        _itemIcons.put(3185,"res/itemIcon/The_Lightbringer.png");
        _itemIcons.put(3184,"res/itemIcon/Entropy.png");
        _itemIcons.put(3180,"res/itemIcon/Odyn's_Veil.png");
        _itemIcons.put(3181,"res/itemIcon/Sanguine_Blade.png");
        _itemIcons.put(3174,"res/itemIcon/Athene's_Unholy_Grail.png");
        _itemIcons.put(3175,"res/itemIcon/Head_of_Kha'Zix.png");
        _itemIcons.put(3172,"res/itemIcon/Zephyr.png");
        _itemIcons.put(3173,"res/itemIcon/Eleisa's_Miracle.png");
        _itemIcons.put(3170,"res/itemIcon/Moonflair_Spellblade.png");
        _itemIcons.put(3171,"res/itemIcon/3171_Bone_Tooth.png");
        _itemIcons.put(3168,"res/itemIcon/3168_Bone_Tooth.png");
        _itemIcons.put(3169,"res/itemIcon/3169_Bone_Tooth.png");
        _itemIcons.put(3097,"res/itemIcon/Emblem_of_Valor.png");
       // _itemIcons.put(3096,"res/itemIcon/Philosopher's_Stone.png");
        _itemIcons.put(3098,"res/itemIcon/Kage's_Lucky_Pick.png");
        _itemIcons.put(3101,"res/itemIcon/Stinger.png");
        _itemIcons.put(3100,"res/itemIcon/Lich_Bane.png");
        _itemIcons.put(3102,"res/itemIcon/Banshee's_Veil.png");
        _itemIcons.put(3089,"res/itemIcon/Rabadon's_Deathcap.png");
        _itemIcons.put(3091,"res/itemIcon/Wit's_End.png");
        _itemIcons.put(3090,"res/itemIcon/Wooglet's_Witchcap.png");
        _itemIcons.put(3093,"res/itemIcon/Avarice_Blade.png");
        _itemIcons.put(3092,"res/itemIcon/Kage's_Last_Breath.png");
        _itemIcons.put(3082,"res/itemIcon/Warden's_Mail.png");
        _itemIcons.put(3083,"res/itemIcon/Warmog's_Armor.png");
        _itemIcons.put(3084,"res/itemIcon/Overlord's_Bloodmail.png");
        _itemIcons.put(3085,"res/itemIcon/Runaan's_Hurricane.png");
        _itemIcons.put(3086,"res/itemIcon/Zeal.png");
        _itemIcons.put(3087,"res/itemIcon/Statikk_Shiv.png");
        _itemIcons.put(3072,"res/itemIcon/The_Bloodthirster.png");
        _itemIcons.put(3074,"res/itemIcon/Ravenous_Hydra.png");
        _itemIcons.put(3075,"res/itemIcon/Thornmail.png");
        _itemIcons.put(3077,"res/itemIcon/Tiamat.png");
        _itemIcons.put(3078,"res/itemIcon/Trinity_Force.png");
        _itemIcons.put(3131,"res/itemIcon/Sword_of_the_Divine.png");
        _itemIcons.put(3128,"res/itemIcon/Deathfire_Grasp.png");
        _itemIcons.put(3135,"res/itemIcon/Void_Staff.png");
        _itemIcons.put(3134,"res/itemIcon/The_Brutalizer.png");
        _itemIcons.put(3132,"res/itemIcon/Heart_of_Gold.png");
        _itemIcons.put(3123,"res/itemIcon/Executioner's_Calling.png");
        _itemIcons.put(3122,"res/itemIcon/Wicked_Hatchet.png");
        _itemIcons.put(3124,"res/itemIcon/Guinsoo's_Rageblade.png");
        _itemIcons.put(3114,"res/itemIcon/Malady.png");
        _itemIcons.put(3115,"res/itemIcon/Nashor's_Tooth.png");
        _itemIcons.put(3116,"res/itemIcon/Rylai's_Crystal_Scepter.png");
        _itemIcons.put(3117,"res/itemIcon/Boots_of_Mobility.png");
        _itemIcons.put(3106,"res/itemIcon/Madred's_Razors.png");
        _itemIcons.put(3107,"res/itemIcon/Runic_Bulwark.png");
        _itemIcons.put(3104,"res/itemIcon/Lord_Van_Damm's_Pillager.png");
        _itemIcons.put(3105,"res/itemIcon/Aegis_of_the_Legion.png");
        _itemIcons.put(3110,"res/itemIcon/Frozen_Heart.png");
        _itemIcons.put(3111,"res/itemIcon/Mercury's_Treads.png");
        _itemIcons.put(3108,"res/itemIcon/Fiendish_Codex.png");
        _itemIcons.put(3004,"res/itemIcon/Manamune.png");
        _itemIcons.put(3005,"res/itemIcon/Atma's_Impaler.png");
        _itemIcons.put(3006,"res/itemIcon/Berserker's_Greaves.png");
        _itemIcons.put(3001,"res/itemIcon/Abyssal_Scepter.png");
        _itemIcons.put(3003,"res/itemIcon/Archangel's_Staff.png");
        _itemIcons.put(3050,"res/itemIcon/Zeke's_Herald.png");
        _itemIcons.put(3041,"res/itemIcon/Mejai's_Soulstealer.png");
        _itemIcons.put(3040,"res/itemIcon/Seraph's_Embrace.png");
        _itemIcons.put(3042,"res/itemIcon/Muramana.png");
        _itemIcons.put(3044,"res/itemIcon/Phage.png");
        _itemIcons.put(3047,"res/itemIcon/Ninja_Tabi.png");
        _itemIcons.put(3046,"res/itemIcon/Phantom_Dancer.png");
        _itemIcons.put(3065,"res/itemIcon/Spirit_Visage.png");
        _itemIcons.put(3067,"res/itemIcon/Kindlegem.png");
        _itemIcons.put(3068,"res/itemIcon/Sunfire_Cape.png");
        _itemIcons.put(3069,"res/itemIcon/Shurelya's_Reverie.png");
        _itemIcons.put(3070,"res/itemIcon/Tear_of_the_Goddess.png");
        _itemIcons.put(3071,"res/itemIcon/The_Black_Cleaver.png");
        _itemIcons.put(3056,"res/itemIcon/Ohmwrecker.png");
        _itemIcons.put(3057,"res/itemIcon/Sheen.png");
        _itemIcons.put(3060,"res/itemIcon/Banner_of_Command.png");
        _itemIcons.put(3022,"res/itemIcon/Frozen_Mallet.png");
        _itemIcons.put(3020,"res/itemIcon/Sorcerer's_Shoes.png");
        _itemIcons.put(3010,"res/itemIcon/Catalyst_the_Protector.png");
        _itemIcons.put(3009,"res/itemIcon/Boots_of_Swiftness.png");
        _itemIcons.put(3035,"res/itemIcon/Last_Whisper.png");
        _itemIcons.put(3037,"res/itemIcon/Mana_Manipulator.png");
        _itemIcons.put(3026,"res/itemIcon/Guardian_Angel.png");
        _itemIcons.put(3027,"res/itemIcon/Rod_of_Ages.png");
        _itemIcons.put(3024,"res/itemIcon/Glacial_Shroud.png");
        _itemIcons.put(3025,"res/itemIcon/Iceborn_Gauntlet.png");
        _itemIcons.put(3031,"res/itemIcon/Infinity_Edge.png");
        _itemIcons.put(3028,"res/itemIcon/Chalice_of_Harmony.png");
        _itemIcons.put(2037,"res/itemIcon/Elixir_of_Fortitude.png");
        _itemIcons.put(2039,"res/itemIcon/Elixir_of_Brilliance.png");
        _itemIcons.put(2040,"res/itemIcon/Ichor_of_Rage.png");
        _itemIcons.put(2041,"res/itemIcon/Crystalline_Flask.png");
        _itemIcons.put(2042,"res/itemIcon/Oracle's_Elixir.png");
        _itemIcons.put(2043,"res/itemIcon/Vision_Ward.png");
        _itemIcons.put(2044,"res/itemIcon/Stealth_Ward.png");
        _itemIcons.put(2045,"res/itemIcon/Ruby_Sightstone.png");
        _itemIcons.put(2047,"res/itemIcon/Oracle's_Extract.png");
        _itemIcons.put(2003,"res/itemIcon/Health_Potion.png");
        _itemIcons.put(2004,"res/itemIcon/Mana_Potion.png");
        _itemIcons.put(2009,"res/itemIcon/Total_Biscuit_of_Rejuvenation.png");
        _itemIcons.put(2010,"res/itemIcon/Total_Biscuit_of_Rejuvenation.png");
        _itemIcons.put(1018,"res/itemIcon/Cloak_of_Agility.png");
        _itemIcons.put(1011,"res/itemIcon/Giant's_Belt.png");
        _itemIcons.put(1001,"res/itemIcon/Boots_of_Speed.png");
        _itemIcons.put(1004,"res/itemIcon/Faerie_Charm.png");
        _itemIcons.put(1006,"res/itemIcon/Rejuvenation_Bead.png");
        _itemIcons.put(3211, "res/itemIcon/Spectre's_Cowl.png");
        _itemIcons.put(2051, "res/itemIcon/Guardian's_Horn.png");
        _itemIcons.put(3301,"res/itemIcon/Ancient_Coin.png");
        _itemIcons.put(3096, "res/itemIcon/Nomad's_Medallion.png");
        _itemIcons.put(3069, "res/itemIcon/Talisman_Of_Ascension.png");
        _itemIcons.put(3303, "res/itemIcon/Spellthief's_Edge.png");
        _itemIcons.put(3098, "res/itemIcon/Frostfang.png");
        _itemIcons.put(3092, "res/itemIcon/Frost_Queen's_Claim.png");
        _itemIcons.put(3341, "res/itemIcon/Sweeping_Lens.png");
        _itemIcons.put(3342, "res/itemIcon/Scrying_Orb.png");
        _itemIcons.put(3340, "res/itemIcon/Warding_Totem.png");
        _itemIcons.put(3351, "res/itemIcon/Greater_Lens.png");
        _itemIcons.put(3364, "res/itemIcon/Oracle's_Lens.png");
        _itemIcons.put(3361, "res/itemIcon/Greater_Stealth_Totem.png");
        _itemIcons.put(3362, "res/itemIcon/Greater_Vision_Totem.png");
        _itemIcons.put(3350, "res/itemIcon/Greater_Totem.png");
        _itemIcons.put(3363, "res/itemIcon/Farsight_Orb.png");
        _itemIcons.put(3352, "res/itemIcon/Greater_Orb");
        _itemIcons.put(3290, "res/itemIcon/Twin_Shadows.png");
        _itemIcons.put(3023, "res/itemIcon/Twin_Shadows.png");
        _itemIcons.put(3112, "res/itemIcon/Orb_of_Winter.png"); 
    }
	
	private JLabel champLabel;
	private JPanel infoPanel;
	private JLabel killsLabel, deathsLabel, assistsLabel, CSLabel, datePlayedLabel;
	private JLabel killCount, deathCount, assistCount, CSCount;
	private JLabel datePlayed;
	private JLabel matchmakingQueueLabel;
	private JPanel killPanel, deathPanel, assistPanel, CSPanel, datePlayedPanel;
	private JLabel item0Label, item1Label, item2Label, item3Label, item4Label, item5Label, trinketLabel;
	private JPanel itemPanel;
	private ArrayList<JLabel> itemLabels;
	public MatchCellRenderer(CellSize size) {
		setLayout(new GridBagLayout());
		setBorder(BorderFactory.createLineBorder(Color.black));
		champLabel = new JLabel();
		
		int fontSize = 0;
		switch(size)
		{
		case SMALL:
			fontSize = 12;
			break;
		case MEDIUM:
			fontSize = 14;
			break;
		case LARGE:
			fontSize = 16;
			break;
		default:
			fontSize = 10;
				
		}
		
		GridBagConstraints constraint = new GridBagConstraints();
		constraint.gridx = 0;
		constraint.gridy = 0;
		constraint.weightx = 0;
		constraint.weighty = 0;
		constraint.gridwidth = 1;
		constraint.anchor = GridBagConstraints.WEST;
		constraint.fill = GridBagConstraints.BOTH;
		add(champLabel, constraint);
		//Override paintComponent so the panel is a gradient
		infoPanel = new JPanel();
		
		//infoPanel.setBackground(new Color(22,54,103));
		infoPanel.setLayout(new GridBagLayout());
		infoPanel.setOpaque(false);
		GridBagConstraints c = new GridBagConstraints();
		
		Font serifFontBold = new Font(Font.SERIF, Font.BOLD, fontSize);
		Font serifFont = new Font(Font.SERIF, Font.PLAIN, fontSize);
		
		killPanel = new JPanel();
		killPanel.setOpaque(false);
		killPanel.setLayout(new GridLayout(1,2));
		killCount = new JLabel();
		killCount.setFont(serifFont);
		killsLabel = new JLabel("Kills: ");
		killsLabel.setForeground(new Color(78,112,143));
		killsLabel.setFont(serifFontBold);
		killPanel.add(killsLabel);
		killPanel.add(killCount);
		
		deathPanel = new JPanel();
		deathPanel.setOpaque(false);
		deathPanel.setLayout(new GridLayout(1,2));
		deathCount = new JLabel();
		deathCount.setFont(serifFont);
		deathsLabel = new JLabel("Deaths: ");
		deathsLabel.setForeground(new Color(78,112,143));
		deathsLabel.setFont(serifFontBold);
		deathPanel.add(deathsLabel);
		deathPanel.add(deathCount);
		
		assistPanel = new JPanel();
		assistPanel.setOpaque(false);
		assistPanel.setLayout(new GridLayout(1,2));
		assistCount = new JLabel();
		assistCount.setFont(serifFont);
		assistsLabel = new JLabel("Assists: ");
		assistsLabel.setForeground(new Color(78,112,143));
		assistsLabel.setFont(serifFontBold);
		assistsLabel.setHorizontalAlignment(SwingConstants.LEFT);
		assistPanel.add(assistsLabel);
		assistPanel.add(assistCount);
		
		CSPanel = new JPanel();
		CSPanel.setOpaque(false);
		CSPanel.setLayout(new GridLayout(1,2));
		CSLabel = new JLabel("Minions Killed: ");
		CSLabel.setFont(serifFontBold);
		CSLabel.setForeground(new Color(78,112,143));
		CSCount = new JLabel();
		CSCount.setFont(serifFont);
		CSPanel.add(CSLabel);
		CSPanel.add(CSCount);
		
		matchmakingQueueLabel = new JLabel();
		matchmakingQueueLabel.setFont(serifFontBold);
		matchmakingQueueLabel.setPreferredSize(new Dimension(243,19));
		
		datePlayedPanel = new JPanel();
		datePlayedPanel.setOpaque(false);
		datePlayedLabel = new JLabel("Date: ");
		datePlayedLabel.setFont(serifFontBold);
		datePlayedLabel.setForeground(new Color(78,112,143));
		datePlayed = new JLabel();
		datePlayed.setFont(serifFont);
		datePlayedPanel.add(datePlayedLabel);
		datePlayedPanel.add(datePlayed);
		
		
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		infoPanel.add(matchmakingQueueLabel, c);
		
		c.gridwidth = 1;
		c.gridy = 1;
		infoPanel.add(killPanel,c);
		
		c.gridy = 2;
		infoPanel.add(deathPanel,c);
		
		c.gridy = 3;
		infoPanel.add(assistPanel,c);
		
		c.gridx = 1;
		c.gridy = 1;
		infoPanel.add(CSPanel,c);
		
		c.gridy = 2;
		c.anchor = GridBagConstraints.WEST;
		infoPanel.add(datePlayedPanel, c);
		
		constraint.gridx = 1;
		constraint.gridy = 0;
		constraint.weightx = 1;
		constraint.weighty = 1;
		constraint.gridwidth = 3;
		constraint.ipadx = 30;
		constraint.anchor = GridBagConstraints.LINE_START;
		constraint.fill = GridBagConstraints.VERTICAL;
		add(infoPanel, constraint);
		
		itemPanel = new JPanel(new GridBagLayout());
		itemPanel.setOpaque(false);
		item0Label = new JLabel();
		item0Label.setMaximumSize(new Dimension(32,32));
		item1Label = new JLabel();
		item1Label.setMaximumSize(new Dimension(32,32));
		item2Label = new JLabel();
		item2Label.setMaximumSize(new Dimension(32,32));
		item3Label = new JLabel();
		item3Label.setMaximumSize(new Dimension(32,32));
		item4Label = new JLabel();
		item4Label.setMaximumSize(new Dimension(32,32));
		item5Label = new JLabel();
		item5Label.setMaximumSize(new Dimension(32,32));
		trinketLabel = new JLabel();
		trinketLabel.setMaximumSize(new Dimension(32,32));
		
		itemLabels = new ArrayList<JLabel>();
		itemLabels.add(item0Label);
		itemLabels.add(item1Label);
		itemLabels.add(item2Label);
		itemLabels.add(item3Label);
		itemLabels.add(item4Label);
		itemLabels.add(item5Label);
		
		GridBagConstraints itemC = new GridBagConstraints();
		itemC.gridwidth = 2;
		itemC.gridheight = 2;
		itemC.gridx = 0;
		itemC.gridy = 0;
		itemC.fill = GridBagConstraints.BOTH;
		itemPanel.add(item0Label, itemC);
		itemC.gridx = 2;
		itemPanel.add(item1Label, itemC);
		itemC.gridx = 4;
		itemPanel.add(item2Label, itemC);
		itemC.gridx = 6;
		itemC.gridy = 1;
		itemC.ipady = 40;
		itemPanel.add(trinketLabel, itemC);
		itemC.ipady = 0;
		itemC.gridx = 0;
		itemC.gridy = 2;
		itemPanel.add(item3Label, itemC);
		itemC.gridx = 2;
		itemPanel.add(item4Label, itemC);
		itemC.gridx = 4;
		itemPanel.add(item5Label, itemC);
		
		constraint.gridx = 2;
		constraint.gridy = 0;
		constraint.weightx = 1;
		constraint.weighty = 1;
		constraint.gridwidth = 3;
		constraint.ipadx = 10;
		constraint.anchor = GridBagConstraints.EAST;
		add(itemPanel, constraint);
		
		
		
		
	}

	@Override
	public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        Color color1 = new Color(241,234,212);
        Color color2 = new Color(222,204,155);
        int w = getWidth();
        int h = getHeight();
        GradientPaint gp = new GradientPaint(
            0, 0, color1, 0, h, color2);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, w, h);
    }
	
	@Override
	public Component getListCellRendererComponent(JList<? extends E> arg0,
			E arg1, int arg2, boolean arg3, boolean arg4) {
		if(arg1 instanceof Match)
		{
				Match matchSelected = (Match)arg1;
				String champIconPath = _champIcons.get(matchSelected.getChampionPlayed());
		
				ImageIcon champIcon = new ImageIcon(champIconPath, matchSelected.getChampionPlayed());
				champLabel.setIcon(champIcon);
				Border iconOutline;
				if(matchSelected.getWin() == 1)
				{
					iconOutline = BorderFactory.createLineBorder(Color.GREEN);
					champLabel.setBorder(iconOutline);
				}
				else if(matchSelected.getWin() == 0)
				{
					iconOutline = BorderFactory.createLineBorder(Color.RED);
					champLabel.setBorder(iconOutline);
				}
				infoPanel.setOpaque(false);
				
				int i = 0;
				ArrayList<Integer> itemsBought = matchSelected.getItemsBought();
				//System.out.println(itemsBought.size());
				//System.out.println(matchSelected.getChampionPlayed());
				for(JLabel itemLabel : itemLabels)
				{
				//	System.out.println("Item bought: " + LeagueItem.getItemNameFromId(itemsBought.get(i)));
				//	System.out.println("Items bought id: " + itemsBought.get(i));
					//String itemPath = _itemIcons.get(getItemIconById(i));
					
					ImageIcon itemIcon = new ImageIcon(getItemIconById(itemsBought.get(i))); 
					itemLabel.setIcon(itemIcon);
					i++;
				}
				ImageIcon trinketIcon = new ImageIcon(_itemIcons.get(matchSelected.getTrinket()));
				trinketLabel.setIcon(trinketIcon);
				
				
				killCount.setText(Integer.toString(matchSelected.getNumKills()));
				deathCount.setText(Integer.toString(matchSelected.getNumDeaths()));
				assistCount.setText(Integer.toString(matchSelected.getNumAssists()));
				CSCount.setText(Integer.toString(matchSelected.getMinionsKilled()));
				matchmakingQueueLabel.setText(fromInternalToReadableMMQueueName(matchSelected.getMatchmakingQueue()));
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mma z");
				datePlayed.setText(dateFormat.format(matchSelected.getDate()));
				
		}
		
		return this;
	}
	
	/**
	 * Takes the string representation of a matchmaking queue enumeration and returns a human-friendly version. 
	 * @param internalQueueName
	 * @return
	 */
	private String fromInternalToReadableMMQueueName(String internalQueueName)
	{
		String externalQueueName = "";
		
		if(internalQueueName.equals("ARAM_UNRANKED_5x5"))
		{
			externalQueueName = "Howling Abyss";
		}
		else if(internalQueueName.equals("ODIN_UNRANKED"))
		{
			externalQueueName = "Dominion";
		}
		else if(internalQueueName.equals("BOT"))
		{
			externalQueueName = "Summoner's Rift (Bot)";
		}
		else if(internalQueueName.equals("NORMAL"))
		{
			externalQueueName = "Summoner's Rift (Normal)";
		}
		else if(internalQueueName.equals("RANKED_SOLO_5x5"))
		{
			externalQueueName = "Summoner's Rift (Ranked Solo Queue)";
		}
		else if(internalQueueName.equals("RANKED_TEAM_3x3"))
		{
			externalQueueName = "Twisted Treeline (Ranked Team Queue)";
		}
		else if(internalQueueName.equals("RANKED_TEAM_5x5"))
		{
			externalQueueName = "Summoner's Rift (Ranked Team Queue)";
		}
		else if(internalQueueName.equals("NORMAL_3x3"))
		{
			externalQueueName = "Twisted Treeline (Normal)";
		}
		else if(internalQueueName.equals("BOT_3x3"))
		{
			externalQueueName = "Twisted Treeline (Bot)";
		}
		else if(internalQueueName.equals("ONEFORALL_5x5"))
		{
			externalQueueName = "One For All";
		}
		else
		{
			externalQueueName = "Gametype Not Found";
		}
		return externalQueueName;
	}
	
	public static Map<String, String> getChampionIconMap()
	{
		return Collections.unmodifiableMap(_champIcons);
	}
	
	public static String getChampIconByName(String champName)
	{
		return _champIcons.get(champName);
	}

	public static String getItemIconById(int itemId)
	{
		//System.out.println(itemId);
		if(_itemIcons.containsKey(itemId))
		{
	//		System.out.println(_itemIcons.get(itemId));
			return _itemIcons.get(itemId);
		}
		else
		{
			System.out.println("Missing item: " + itemId);
			return "empty";
		}
	}
}
