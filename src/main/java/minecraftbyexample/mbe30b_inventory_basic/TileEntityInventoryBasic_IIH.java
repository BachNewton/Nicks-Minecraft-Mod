package minecraftbyexample.mbe30b_inventory_basic;

import minecraftbyexample.mbe32_inventory_item.ItemStackHandlerFlowerBag;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * User: brandon3055 & TGG
 * Date: 06/01/2015
 *
 * This is a simple tile entity that can store 9 ItemStacks
 */
public class TileEntityInventoryBasic_IIH extends TileEntity implements INamedContainerProvider {
	public static final int NUMBER_OF_SLOTS = 9;

	public TileEntityInventoryBasic_IIH()
	{
    super(StartupCommon.tileEntityTypeMBE30_IIH);
//    chestContents = ChestContents_IIH.createForTileEntity(NUMBER_OF_SLOTS,
//            this::canPlayerAccessInventory, this::markDirty);
	}

	private static final String CHESTCONTENTS_INVENTORY_TAG = "contents";

	// This is where you save any data that you don't want to lose when the tile entity unloads
	// In this case, it saves the chestContents, which contains the ItemStacks stored in the chest
	@Override
	public CompoundNBT write(CompoundNBT parentNBTTagCompound)
	{
		super.write(parentNBTTagCompound); // The super call is required to save and load the tileEntity's location
    CompoundNBT inventoryNBT = chestContents.serializeNBT();
    parentNBTTagCompound.put(CHESTCONTENTS_INVENTORY_TAG, inventoryNBT);
		return parentNBTTagCompound;
	}

	// This is where you load the data that you saved in write
	@Override
	public void read(BlockState blockState, CompoundNBT parentNBTTagCompound)
	{
		super.read(blockState, parentNBTTagCompound); // The super call is required to save and load the tiles location
    CompoundNBT inventoryNBT = parentNBTTagCompound.getCompound(CHESTCONTENTS_INVENTORY_TAG);
    chestContents.deserializeNBT(inventoryNBT);
    if (chestContents.getSizeInventory() != NUMBER_OF_SLOTS)
      throw new IllegalArgumentException("Corrupted NBT: Number of inventory slots did not match expected.");
	}

  // When the world loads from disk, the server needs to send the TileEntity information to the client
  //  it uses getUpdatePacket(), getUpdateTag(), onDataPacket(), and handleUpdateTag() to do this:
  //  getUpdatePacket() and onDataPacket() are used for one-at-a-time TileEntity updates
  //  getUpdateTag() and handleUpdateTag() are used by vanilla to collate together into a single chunk update packet
  //  Your container may still appear to work even if you forget to implement these methods, because when you open the
  //    container using the GUI it takes the information from the server, but anything on the client
  //   side that looks inside the tileEntity (for example: to change the rendering) won't see anything.
  @Override
  @Nullable
  public SUpdateTileEntityPacket getUpdatePacket()
  {
    CompoundNBT nbtTagCompound = new CompoundNBT();
    write(nbtTagCompound);
    int tileEntityType = 42;  // arbitrary number; only used for vanilla TileEntities.  You can use it, or not, as you want.
    return new SUpdateTileEntityPacket(this.pos, tileEntityType, nbtTagCompound);
  }

  @Override
  public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
    BlockState blockState = world.getBlockState(pos);
    read(blockState, pkt.getNbtCompound());
  }

  /* Creates a tag containing all of the TileEntity information, used by vanilla to transmit from server to client
   *  For this example - Do nothing extra!   The client does not need to know what the contents of the chest are.
   *  If the client did need to know, for example like the vanilla campfire which renders the "contents" of the chest,
   *  you would transmit that information here.
   */
  @Override
  public CompoundNBT getUpdateTag()
  {
    IT APPEARS THAT FORGE WRITES THE CAPABILITY EVEN FOR PACKETS
    return super.getUpdateTag();
  }

  /* Populates this TileEntity with information from the tag, used by vanilla to transmit from server to client
   */
  @Override
  public void handleUpdateTag(BlockState blockState, CompoundNBT tag)
  {
    super.handleUpdateTag(blockState, tag);
  }

  /**
   * When this tile entity is destroyed, drop all of its contents into the world
   * @param world
   * @param blockPos
   */
	public void dropAllContents(World world, BlockPos blockPos) {
	  ItemStackHandler inventory = getInventory();
    double x = blockPos.getX();
    double y = blockPos.getY();
    double z = blockPos.getZ();
	  for (int i = 0; i < inventory.getSlots(); ++i) {
  	  InventoryHelper.spawnItemStack(world, x, y, z, inventory.getStackInSlot(i));
    }
  }

  // Return true if the given player is able to use this block. In this case it checks that
  // 1) the world tileentity hasn't been replaced in the meantime, and
  // 2) the player isn't too far away from the centre of the block
  public boolean canPlayerAccessInventory(PlayerEntity player) {
    if (this.world.getTileEntity(this.pos) != this) return false;
    final double X_CENTRE_OFFSET = 0.5;
    final double Y_CENTRE_OFFSET = 0.5;
    final double Z_CENTRE_OFFSET = 0.5;
    final double MAXIMUM_DISTANCE_BLOCKS = 8.0;
    final double MAXIMUM_DISTANCE_SQ = MAXIMUM_DISTANCE_BLOCKS * MAXIMUM_DISTANCE_BLOCKS;
    return player.getDistanceSq(pos.getX() + X_CENTRE_OFFSET, pos.getY() + Y_CENTRE_OFFSET, pos.getZ() + Z_CENTRE_OFFSET) < MAXIMUM_DISTANCE_SQ;
  }

  // -----------
  // retrieve our chest contents (create it if necessary, i.e. if it hasn't been opened before)
  private ItemStackHandler getInventory() {return itemStackHandlerLazyOptional.orElseThrow(IllegalStateException::new);}

  @Override
  public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> cap, Direction side) {
    if (!this.removed && cap == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
      return (LazyOptional<T>)this.itemStackHandlerLazyOptional;
    }
    return super.getCapability(cap, side);
  }

  private ItemStackHandler createItemStackHandler() {
	  return new ItemStackHandler(NUMBER_OF_SLOTS);
  }

//  /**
//   * Retrieves the ItemStackHandlerFlowerBag for this itemStack (retrieved from the Capability)
//   * @param itemStack
//   * @return
//   */
//  private static ItemStackHandlerFlowerBag getItemStackHandlerFlowerBag(ItemStack itemStack) {
//    IItemHandler flowerBag = itemStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
//    if (flowerBag == null || !(flowerBag instanceof ItemStackHandlerFlowerBag)) {
//      LOGGER.error("ItemFlowerBag did not have the expected ITEM_HANDLER_CAPABILITY");
//      return new ItemStackHandlerFlowerBag(1);
//    }
//    return (ItemStackHandlerFlowerBag)flowerBag;
//  }
  private LazyOptional<ItemStackHandler> itemStackHandlerLazyOptional = LazyOptional.of(this::createItemStackHandler);

  // -------------  The following two methods are used to make the TileEntity perform as a NamedContainerProvider, i.e.
  //  1) Provide a name used when displaying the container, and
  //  2) Creating an instance of container on the server, and linking it to the inventory items stored within the TileEntity

  /**
   *  standard code to look up what the human-readable name is.
   *  Can be useful when the tileentity has a customised name (eg "David's footlocker")
    */
	@Override
	public ITextComponent getDisplayName() {
    return new TranslationTextComponent("container.minecraftbyexample.mbe30b_container_registry_name");
	}

  /**
   * The name is misleading; createMenu has nothing to do with creating a Screen, it is used to create the Container on the server only
   * @param windowID
   * @param playerInventory
   * @param playerEntity
   * @return
   */
  @Nullable
  @Override
  public Container createMenu(int windowID, PlayerInventory playerInventory, PlayerEntity playerEntity) {
    return ContainerBasic_IIH.createContainerServerSide(windowID, playerInventory, getInventory(),
                                                        this::canPlayerAccessInventory, this::markDirty);
  }

//  private final ChestContents_IIH chestContents; // holds the ItemStacks in the Chest
}
