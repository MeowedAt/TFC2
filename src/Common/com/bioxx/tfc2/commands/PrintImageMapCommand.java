package com.bioxx.tfc2.commands;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Vector;

import javax.imageio.ImageIO;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.Point;
import com.bioxx.jmapgen.attributes.Attribute;
import com.bioxx.jmapgen.attributes.RiverAttribute;
import com.bioxx.jmapgen.dungeon.*;
import com.bioxx.jmapgen.dungeon.Dungeon.DungeonDoor;
import com.bioxx.jmapgen.dungeon.Dungeon.DungeonLevel;
import com.bioxx.jmapgen.dungeon.Dungeon.DungeonRect;
import com.bioxx.jmapgen.dungeon.Dungeon.DungeonRoom;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.jmapgen.graph.Corner;
import com.bioxx.jmapgen.pathfinding.Path;
import com.bioxx.jmapgen.pathfinding.PathNode;
import com.bioxx.libnoise.NoiseQuality;
import com.bioxx.libnoise.model.Plane;
import com.bioxx.libnoise.module.Cache;
import com.bioxx.libnoise.module.modifier.Clamp;
import com.bioxx.libnoise.module.modifier.Curve;
import com.bioxx.libnoise.module.modifier.ScaleBias;
import com.bioxx.libnoise.module.modifier.ScalePoint;
import com.bioxx.libnoise.module.source.Perlin;
import com.bioxx.tfc2.world.WorldGen;

public class PrintImageMapCommand extends CommandBase
{
	static Color[] colorMap = new Color[256];
	public PrintImageMapCommand()
	{
		for(int i = 0; i < 256; i++)
		{
			colorMap[i] = Color.getColor("", (i << 16) + (i << 8) + i);
		}
	}
	@Override
	public String getCommandName()
	{
		return "pi";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] params)
	{
		EntityPlayerMP player = null;
		try {
			player = getCommandSenderAsPlayer(sender);
		} catch (PlayerNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		WorldServer world = server.worldServerForDimension(player.getEntityWorld().provider.getDimension());

		if(params.length >= 2)
		{
			String name = params[1];
			if(params[0].equals("elev"))
			{
				drawElevImage((int)Math.floor(player.posX), (int)Math.floor(player.posZ), world, name);
			}
			else if(params[0].equals("biome"))
			{
				drawMapImage((int)Math.floor(player.posX), (int)Math.floor(player.posZ), world, name);
			}
			else if(params[0].equals("canyon"))
			{
				int size = 4096;
				try 
				{
					File outFile = new File(name+".png");
					BufferedImage outBitmap = new BufferedImage(size,size,BufferedImage.TYPE_INT_RGB);
					Graphics2D graphics = (Graphics2D) outBitmap.getGraphics();
					graphics.clearRect(0, 0, size, size);
					System.out.println(name+".png");
					float perc = 0.1f;
					float count = 0;
					int xM = ((int)Math.floor(player.posX) >> 12);
					int zM = ((int)Math.floor(player.posZ) >> 12);
					IslandMap map = WorldGen.getInstance().getIslandMap(xM, zM);
					Point p;
					Center c;
					for(int z = 0; z < size; z++)
					{
						for(int x = 0; x < size; x++)
						{
							p = new Point(x,z);
							count++;
							c = map.getClosestCenter(p);
							if(c.hasAttribute(Attribute.Gorge))
								graphics.setColor(Color.white);	
							else
								graphics.setColor(Color.black);	
							graphics.drawRect(x, z, 1, 1);
							if(count / (size*size) > perc)
							{
								System.out.println((int)(perc*100)+"%");
								perc+=0.1f;
							}
						}
					}
					System.out.println(name+".png Done!");
					ImageIO.write(outBitmap, "PNG", outFile);
				}
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
			else if(params[0].equals("noise"))
			{
				int size = params.length >= 3 ? Integer.parseInt(params[2]) : 512;
				drawNoiseImage((int)Math.floor(player.posX), (int)Math.floor(player.posZ), size, world, name);
			}
			else if(params[0].equals("path"))
			{
				int size = 4096;
				try 
				{
					File outFile = new File(name+".png");
					BufferedImage outBitmap = new BufferedImage(size,size,BufferedImage.TYPE_INT_RGB);
					Graphics2D graphics = (Graphics2D) outBitmap.getGraphics();
					graphics.clearRect(0, 0, size, size);
					System.out.println(name+".png");
					float perc = 0.1f;
					float count = 0;
					int xM = ((int)Math.floor(player.posX) >> 12);
					int zM = ((int)Math.floor(player.posZ) >> 12);
					IslandMap map = WorldGen.getInstance().getIslandMap(xM, zM);
					Center closest = map.getClosestCenter(new Point((int)Math.floor(player.posX) & 4095, (int)Math.floor(player.posZ) & 4095));
					Vector<Center> land = map.landCenters(map.centers);
					Center end = land.get(world.rand.nextInt(land.size()));
					Path path = map.pathfinder.findPath(closest, end);
					if(path == null)
					{
						System.out.println("Failed to find path");
						return;
					}
					for(PathNode pn : path.path)
					{
						count++;
						graphics.setColor(Color.white);		
						if(pn.center == closest)
							graphics.setColor(Color.red);	
						if(pn.center == end)
							graphics.setColor(Color.blue);	
						Polygon poly = new Polygon();

						for(Corner cn : pn.center.corners)
						{
							poly.addPoint((int)cn.point.x, (int)cn.point.y);
						}
						graphics.fillPolygon(poly);

					}
					System.out.println(name+".png Done!");
					ImageIO.write(outBitmap, "PNG", outFile);
				}
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
			else if(params[0].equals("moisture"))
			{
				int size = 4096;
				try 
				{
					File outFile = new File(name+".png");
					BufferedImage outBitmap = new BufferedImage(size,size,BufferedImage.TYPE_INT_RGB);
					Graphics2D graphics = (Graphics2D) outBitmap.getGraphics();
					graphics.clearRect(0, 0, size, size);
					System.out.println(name+".png");
					float perc = 0.1f;
					float count = 0;
					int xM = ((int)Math.floor(player.posX) >> 12);
					int zM = ((int)Math.floor(player.posZ) >> 12);
					IslandMap map = WorldGen.getInstance().getIslandMap(xM, zM);
					Polygon poly;
					for(Center c : map.centers)
					{
						count++;
						int color = (int)(c.getMoistureRaw() * 255);
						graphics.setColor(colorMap[color]);		
						poly = new Polygon();
						for(Corner cn : c.corners)
						{
							poly.addPoint((int)cn.point.x, (int)cn.point.y);
						}
						graphics.fillPolygon(poly);
						graphics.setColor(Color.black);	
						graphics.drawPolygon(poly);
					}

					for(Center c : map.centers)
					{
						if(c.hasAttribute(Attribute.River))
						{
							RiverAttribute a = (RiverAttribute) c.getAttribute(Attribute.River);
							if(a.getDownRiver() != null)
							{
								graphics.setColor(Color.cyan);	
								graphics.drawLine((int)c.point.x, (int)c.point.y, (int)a.getDownRiver().point.x, (int)a.getDownRiver().point.y);
							}
						}

					}
					System.out.println(name+".png Done!");
					ImageIO.write(outBitmap, "PNG", outFile);
				}
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
			else if(params[0].equals("test"))
			{
				int size = 16;
				try 
				{
					File outFile = new File(name+".png");
					BufferedImage outBitmap = new BufferedImage(size,size,BufferedImage.TYPE_INT_RGB);
					Graphics2D graphics = (Graphics2D) outBitmap.getGraphics();
					graphics.clearRect(0, 0, size, size);
					System.out.println(name+".png");
					float perc = 0.1f;
					float count = 0;
					int xM = ((int)Math.floor(player.posX) >> 12);
					int zM = ((int)Math.floor(player.posZ) >> 12);
					IslandMap map = WorldGen.getInstance().getIslandMap(xM, zM);

					Perlin pe = new Perlin();
					pe.setSeed(0);
					pe.setFrequency (1f/2f);
					pe.setLacunarity(5);
					pe.setNoiseQuality (com.bioxx.libnoise.NoiseQuality.BEST);

					ScaleBias sb2 = new ScaleBias();
					sb2.setSourceModule(0, pe);
					//Noise is normally +-2 so we scale by 0.5 to make it +-1.0
					sb2.setBias(0.5);
					sb2.setScale(0.25);
					Plane p = new Plane(sb2);

					for(int y = 0; y < size; y++)
					{
						for(int x = 0; x < size; x++)
						{
							double val = p.GetValue(x, y);
							int rain = Math.min((int)(val * 255), 255);
							graphics.setColor(colorMap[rain]);	
							graphics.drawRect(x, y, x+1, y+1);
						}
					}

					//rainmap
					/*for(int x = 0; x < size; x++)
					{
						//double val = line.getValue((double)(world.getWorldTime() >> 9)+x, (xM >> 12) * 1000000, (zM >> 12) * 1000000);
						double val = WeatherManager.getInstance().rainModelSummer.getValue(Timekeeper.getInstance().getTotalHalfHours()+x, xM * 1000000, zM * 1000000);
						int rain = (int)(val * 255);
						graphics.setColor(colorMap[rain]);	
						graphics.drawRect(x, 0, x+1, 512);
					}*/


					System.out.println(name+".png Done!");
					ImageIO.write(outBitmap, "PNG", outFile);
				}
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
			else if(params[0].equals("dungeon"))
			{
				int xM = ((int)Math.floor(player.posX) >> 12);
				int zM = ((int)Math.floor(player.posZ) >> 12);
				IslandMap map = WorldGen.getInstance().getIslandMap(xM, zM);
				drawDungeon(name, map);
			}
		}
	}

	public void drawDungeon(String name, IslandMap map)
	{
		Dungeon d = map.dungeons.get(0);
		//d.generate(0, map.centers.get(0));
		int size = 1024;
		int size2 = size/2;
		try 
		{
			int count = 0;
			for(DungeonLevel level : d.levels)
			{
				File outFile = new File(name+"-"+count+".png");
				BufferedImage outBitmap = new BufferedImage(size,size,BufferedImage.TYPE_INT_RGB);
				Graphics2D graphics = (Graphics2D) outBitmap.getGraphics();
				graphics.clearRect(0, 0, size, size);
				graphics.setColor(Color.white);

				for(DungeonRoom room : level.rooms)
				{
					for(DungeonRect dun : room.rects)
					{
						graphics.setColor(Color.darkGray);
						graphics.fillRect(dun.X+size2-(int)d.entrance.point.getX(), dun.Z+size2-(int)d.entrance.point.getZ(), dun.width, dun.height);
					}
					if(room.doors != null)
						for(DungeonDoor door : room.doors)
						{
							graphics.setColor(Color.white);
							graphics.fillRect(door.location.getX()+size2-(int)d.entrance.point.getX(), door.location.getZ()+size2-(int)d.entrance.point.getZ(), 1, 1);
						}
				}

				System.out.println(name+"-"+count+".png Done!");
				ImageIO.write(outBitmap, "PNG", outFile);
				count++;
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}



	public static void drawMapImage(int xCoord, int zCoord, World world, String name)
	{
		int size = 4096;
		try 
		{
			File outFile = new File(name+".png");
			BufferedImage outBitmap = new BufferedImage(size,size,BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics = (Graphics2D) outBitmap.getGraphics();
			graphics.clearRect(0, 0, size, size);
			System.out.println(name+".png");
			float perc = 0.1f;
			float count = 0;
			int xM = (xCoord >> 12);
			int zM = (zCoord >> 12);
			IslandMap map = WorldGen.getInstance().getIslandMap(xM, zM);
			Polygon poly;
			for(Center c : map.centers)
			{
				count++;
				graphics.setColor(c.biome.color);		
				poly = new Polygon();
				for(Corner cn : c.corners)
				{
					poly.addPoint((int)cn.point.x, (int)cn.point.y);
				}
				graphics.fillPolygon(poly);
				graphics.setColor(Color.black);	
				graphics.drawPolygon(poly);
			}

			for(Center c : map.centers)
			{
				if(c.hasAttribute(Attribute.River))
				{
					RiverAttribute a = (RiverAttribute) c.getAttribute(Attribute.River);
					if(a.getDownRiver() != null)
					{
						graphics.setColor(Color.cyan);	
						graphics.drawLine((int)c.point.x, (int)c.point.y, (int)a.getDownRiver().point.x, (int)a.getDownRiver().point.y);
					}
				}

			}
			Point p = new Point(xCoord, zCoord).toIslandCoord();
			graphics.setColor(Color.RED);	
			poly = new Polygon();
			Center c = map.getClosestCenter(p);
			for(Corner cn : c.corners)
			{
				poly.addPoint((int)cn.point.x, (int)cn.point.y);
			}
			graphics.fillPolygon(poly);

			System.out.println(name+".png Done!");
			ImageIO.write(outBitmap, "PNG", outFile);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	public static void drawElevImage(int xCoord, int zCoord, World world, String name)
	{
		int size = 4096;
		try 
		{
			File outFile = new File(name+".png");
			BufferedImage outBitmap = new BufferedImage(size,size,BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics = (Graphics2D) outBitmap.getGraphics();
			graphics.clearRect(0, 0, size, size);
			System.out.println(name+".png");
			float perc = 0.1f;
			float count = 0;
			int xM = (xCoord >> 12);
			int zM = (zCoord >> 12);
			IslandMap map = WorldGen.getInstance().getIslandMap(xM, zM);
			Polygon poly;
			for(Center c : map.centers)
			{
				count++;
				int elev = Math.min(Math.max((int)(c.getElevation()*255), 0), colorMap.length-1);
				graphics.setColor(colorMap[elev]);		
				poly = new Polygon();
				for(Corner cn : c.corners)
				{
					poly.addPoint((int)cn.point.x, (int)cn.point.y);
				}
				graphics.fillPolygon(poly);

			}
			System.out.println(name+".png Done!");
			ImageIO.write(outBitmap, "PNG", outFile);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	public static void drawNoiseImage(int xCoord, int zCoord, int size, World world, String name)
	{
		try 
		{
			File outFile = new File(name+".png");
			BufferedImage outBitmap = new BufferedImage(size,size,BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics = (Graphics2D) outBitmap.getGraphics();
			graphics.clearRect(0, 0, size, size);
			System.out.println(name+".png");
			float perc = 0.1f;
			float count = 0;

			Perlin pe = new Perlin();
			pe.setSeed (0);
			pe.setFrequency (0.03125);
			pe.setOctaveCount (4);
			pe.setNoiseQuality (NoiseQuality.BEST);

			ScalePoint sp = new ScalePoint();
			sp.setSourceModule(0, pe);
			sp.setxScale(.1);
			sp.setzScale(.1);

			//The scalebias makes our noise fit the range 0-1
			ScaleBias sb = new ScaleBias(sp);
			//Noise is normally +-2 so we scale by 0.25 to make it +-0.5
			sb.setScale(0.25);
			//Next we offset by +0.5 which makes the noise 0-1
			sb.setBias(0.5);

			Curve curveModule = new Curve();
			curveModule.setSourceModule(0, sb);
			curveModule.AddControlPoint(0, 0);
			curveModule.AddControlPoint(0.35, 0.1);
			curveModule.AddControlPoint(0.75, 0.9);
			curveModule.AddControlPoint(1, 1);

			Clamp clampModule = new Clamp();
			clampModule.setSourceModule(0, curveModule);
			clampModule.setLowerBound(0);
			clampModule.setUpperBound(1);

			Cache cacheModule = new Cache();
			cacheModule.setSourceModule(0, clampModule);
			Plane heightPlane = new Plane(cacheModule);

			for(int z = 0; z < size; z++)
			{
				for(int x = 0; x < size; x++)
				{
					count++;
					double n = heightPlane.GetValue(xCoord-(size/2)+x, zCoord-(size/2)+z);
					if(n >= -2 && n <= 2)
					{
						int h = (int)(255*n);
						graphics.setColor(colorMap[h]);	
						graphics.drawRect(z, x, 1, 1);
						if(count / (size*size) > perc)
						{
							System.out.println((int)(perc*100)+"%");
							perc+=0.1f;
						}
					}
					else
					{
						//System.out.println("Error");
					}
				}
			}
			System.out.println(name+".png Done!");
			ImageIO.write(outBitmap, "PNG", outFile);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	@Override
	public String getCommandUsage(ICommandSender icommandsender)
	{
		return "";
	}

}
