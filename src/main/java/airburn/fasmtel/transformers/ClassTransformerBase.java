package airburn.fasmtel.transformers;

import com.google.common.collect.Lists;
import net.minecraft.launchwrapper.Launch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class ClassTransformerBase {
	private static final Logger DEFAULT_LOGGER = LogManager.getLogger("fasmtel");
	private final byte[] basicClass;
	private final int flags;
	private final List<MethodData> methods;

	public ClassTransformerBase(byte[] basicClass, int flags, MethodData... methods) {
		this.basicClass = basicClass;
		this.flags = flags;
		this.methods = Lists.newArrayList(methods);
	}

	public ClassTransformerBase(byte[] basicClass, MethodData... methods) {
		this(basicClass, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES, methods);
	}

	public final byte[] transform() {
		ClassNode cnode = new ClassNode();
		ClassReader cr = new ClassReader(basicClass);
		cr.accept(cnode, 0);
		for(MethodNode mnode: cnode.methods) {
			for(MethodData method: methods) {
				if(method.isVisited() || !method.isRightMethod(mnode)) {
					continue;
				}
				if(!method.apply(mnode)) {
					getLogger().error("Error transforming method '" + mnode.name + "' in class '" + cr.getClassName() + "', transformers will not be applied.");
					return basicClass;
				}
				method.setVisited();
				getLogger().info("Method '" + mnode.name + "' in class '" + cr.getClassName() + "' transformed successfully.");
			}
		}
		for(MethodData method: methods) {
			if(!method.isVisited()) {
				getLogger().warn("Method '" + method.deobfName + "' was defined for transform for class '" + cr.getClassName() + "' but was not visited.");
			}
		}
		ClassWriter cw = new ClassWriter(flags);
		cnode.accept(cw);
		return cw.toByteArray();
	}
	
	protected Logger getLogger() {
		return DEFAULT_LOGGER;
	}

	protected static String getPath(Class<?> clazz) {
		return clazz.getName().replace(".", "/");
	}

	public static boolean isObfuscated() {
		return Launch.blackboard.get("fml.deobfuscatedEnvironment").equals(false);
	}

	@SafeVarargs
	protected static AbstractInsnNode getNode(InsnList list, Predicate<AbstractInsnNode>... conditions) {
		for(AbstractInsnNode node: new InsnListIterable(list)) {
			if(testNode(node, conditions)) {
				return node;
			}
		}
		return null;
	}

	@SafeVarargs
	protected static AbstractInsnNode getNode(MethodNode mnode, Predicate<AbstractInsnNode>... condition) {
		return getNode(mnode.instructions, condition);
	}

	@SafeVarargs
	protected static AbstractInsnNode getNode(Iterator<AbstractInsnNode> iterator, Predicate<AbstractInsnNode>... conditions) {
		AbstractInsnNode node;
		while(iterator.hasNext()) {
			if(testNode(node = iterator.next(), conditions)) {
				return node;
			}
		}
		return null;
	}

	@SafeVarargs
	private static boolean testNode(AbstractInsnNode node, Predicate<AbstractInsnNode>... conditions) {
		return Arrays.stream(conditions).allMatch(t -> t.test(node));
	}

	protected static InsnList list(AbstractInsnNode... nodes) {
		InsnList list = new InsnList();
		for(AbstractInsnNode node: nodes) {
			list.add(node);
		}
		return list;
	}

	protected static AbstractInsnNode previous(AbstractInsnNode node, Predicate<AbstractInsnNode> condition) {
		while(node.getPrevious() != null) {
			node = node.getPrevious();
			if(condition.test(node)) {
				return node;
			}
		}
		return null;
	}

	protected static AbstractInsnNode previous(AbstractInsnNode node, int skips) {
		for(int i = 0; i < skips; ++i) {
			node = node.getPrevious();
		}
		return node;
	}

	protected static AbstractInsnNode next(AbstractInsnNode node, Predicate<AbstractInsnNode> condition) {
		while(node.getNext() != null) {
			node = node.getNext();
			if(condition.test(node)) {
				return node;
			}
		}
		return null;
	}

	protected static AbstractInsnNode next(AbstractInsnNode node, int skips) {
		for(int i = 0; i < skips; ++i) {
			node = node.getNext();
		}
		return node;
	}

	// Helper aggregator class, cuz InsnList HAS iterator, but does not implement Iterable<?>
	private static class InsnListIterable implements Iterable<AbstractInsnNode> {

		private final InsnList list;

		InsnListIterable(InsnList list) {
			this.list = list;
		}

		@Override
		public Iterator<AbstractInsnNode> iterator() {
			return list.iterator();
		}

	}

}
