/* $Revision$ $Author$ $Date$    
 * 
 * Copyright (C) 1997-2007  Egon Willighagen <egonw@users.sf.net>
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA. 
 */
package org.openscience.cdk.fingerprint;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.BitSet;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.AtomContainerAtomPermutor;
import org.openscience.cdk.graph.AtomContainerBondPermutor;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.io.MDLRXNV2000Reader;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * @cdk.module test-standard
 */
public class FingerprinterTest extends AbstractFingerprinterTest {

	boolean standAlone = false;
	private static ILoggingTool logger =
        LoggingToolFactory.createLoggingTool(FingerprinterTest.class);

	public IFingerprinter getFingerprinter() {
	    return new Fingerprinter();
	}

	@Test public void testRegression() throws Exception {
		IMolecule mol1 = MoleculeFactory.makeIndole();
		IMolecule mol2 = MoleculeFactory.makePyrrole();
		Fingerprinter fingerprinter = new Fingerprinter();
		BitSet bs1 = fingerprinter.getFingerprint(mol1);
		Assert.assertEquals("Seems the fingerprint code has changed. This will cause a number of other tests to fail too!", 33, bs1.cardinality());
		BitSet bs2 = fingerprinter.getFingerprint(mol2);
		Assert.assertEquals("Seems the fingerprint code has changed. This will cause a number of other tests to fail too!", 13, bs2.cardinality());
	}

	@Test public void testGetSize() throws java.lang.Exception {
		IFingerprinter fingerprinter = new Fingerprinter(512);
		Assert.assertNotNull(fingerprinter);
		Assert.assertEquals(512, fingerprinter.getSize());
	}

	@Test public void testGetSearchDepth() throws java.lang.Exception {
		Fingerprinter fingerprinter = new Fingerprinter(512,3);
		Assert.assertNotNull(fingerprinter);
		Assert.assertEquals(3, fingerprinter.getSearchDepth());
	}

	@Test public void testGetFingerprint_IAtomContainer() throws java.lang.Exception
	{
		Fingerprinter fingerprinter = new Fingerprinter();

		Molecule mol = MoleculeFactory.makeIndole();
		BitSet bs = fingerprinter.getFingerprint(mol);
		Assert.assertNotNull(bs);
		Assert.assertEquals(fingerprinter.getSize(), bs.size());
	}

	@Test public void testFingerprinter() throws java.lang.Exception
	{
		Fingerprinter fingerprinter = new Fingerprinter();
		Assert.assertNotNull(fingerprinter);

		Molecule mol = MoleculeFactory.makeIndole();
		BitSet bs = fingerprinter.getFingerprint(mol);
		Molecule frag1 = MoleculeFactory.makePyrrole();
		BitSet bs1 = fingerprinter.getFingerprint(frag1);
		Assert.assertTrue(FingerprinterTool.isSubset(bs, bs1));
	}

	@Test public void testFingerprinter_int() throws java.lang.Exception
	{
		Fingerprinter fingerprinter = new Fingerprinter(512);
		Assert.assertNotNull(fingerprinter);

		Molecule mol = MoleculeFactory.makeIndole();
		BitSet bs = fingerprinter.getFingerprint(mol);
		Molecule frag1 = MoleculeFactory.makePyrrole();
		BitSet bs1 = fingerprinter.getFingerprint(frag1);
		Assert.assertTrue(FingerprinterTool.isSubset(bs, bs1));
	}

	@Test public void testFingerprinter_int_int() throws java.lang.Exception
	{
		Fingerprinter fingerprinter = new Fingerprinter(1024,7);
		Assert.assertNotNull(fingerprinter);

		Molecule mol = MoleculeFactory.makeIndole();
		BitSet bs = fingerprinter.getFingerprint(mol);
		Molecule frag1 = MoleculeFactory.makePyrrole();
		BitSet bs1 = fingerprinter.getFingerprint(frag1);
		Assert.assertTrue(FingerprinterTool.isSubset(bs, bs1));
	}

  @Test public void testFingerprinterBitSetSize() throws Exception {
    Fingerprinter fingerprinter = new Fingerprinter(1024,7);
    Assert.assertNotNull(fingerprinter);
    Molecule mol = MoleculeFactory.makeIndole();
    BitSet bs = fingerprinter.getFingerprint(mol);
    Assert.assertEquals(994, bs.length()); // highest set bit
    Assert.assertEquals(1024, bs.size()); // actual bit set size
  }

	/**
	 * @cdk.bug 1851202
	 */
	@Test
    public void testBug1851202() throws Exception {
        String filename1 = "data/mdl/0002.stg01.rxn";
        logger.info("Testing: " + filename1);
        InputStream ins1 = this.getClass().getClassLoader().getResourceAsStream(filename1);
        MDLRXNV2000Reader reader = new MDLRXNV2000Reader(ins1, Mode.STRICT);
        IReaction reaction = (IReaction)reader.read(new Reaction());
        Assert.assertNotNull(reaction);

        IAtomContainer reactant = reaction.getReactants().getAtomContainer(0);
        IAtomContainer product = reaction.getProducts().getAtomContainer(0);

        Fingerprinter fingerprinter = new Fingerprinter(64*26,8);
            BitSet bs1 = fingerprinter.getFingerprint(reactant);
        Assert.assertNotNull(bs1);
        BitSet bs2 = fingerprinter.getFingerprint(product);
        Assert.assertNotNull(bs2);
	}

    /**
     * @cdk.bug 2819557
     * @throws org.openscience.cdk.exception.CDKException
     */
    @Test
    public void testBug2819557() throws CDKException {
        Molecule butane = makeButane();
        Molecule propylAmine = makePropylAmine();

        Fingerprinter fp = new Fingerprinter();
        BitSet b1 = fp.getFingerprint(butane);
        BitSet b2 = fp.getFingerprint(propylAmine);

        Assert.assertFalse("butane should not be a substructure of propylamine", FingerprinterTool.isSubset(b2, b1));
    }

    @Test
    public void testBondPermutation() throws CDKException {
        IMolecule pamine = makePropylAmine();
        Fingerprinter fp = new Fingerprinter();
        BitSet bs1 = fp.getFingerprint(pamine);

        AtomContainerBondPermutor acp = new AtomContainerBondPermutor(pamine);
        while (acp.hasNext()) {
            IAtomContainer container = acp.next();
            BitSet bs2 = fp.getFingerprint(container);
            Assert.assertTrue(bs1.equals(bs2));
        }
    }

    @Test
    public void testAtomPermutation() throws CDKException {
        IMolecule pamine = makePropylAmine();
        Fingerprinter fp = new Fingerprinter();
        BitSet bs1 = fp.getFingerprint(pamine);

        AtomContainerAtomPermutor acp = new AtomContainerAtomPermutor(pamine);
        while (acp.hasNext()) {
            IAtomContainer container = acp.next();
            BitSet bs2 = fp.getFingerprint(container);
            Assert.assertTrue(bs1.equals(bs2));
        }
    }

    @Test
    public void testBondPermutation2() throws CDKException {
        IMolecule pamine = MoleculeFactory.makeCyclopentane();
        Fingerprinter fp = new Fingerprinter();
        BitSet bs1 = fp.getFingerprint(pamine);

        AtomContainerBondPermutor acp = new AtomContainerBondPermutor(pamine);
        while (acp.hasNext()) {
            IAtomContainer container = acp.next();
            BitSet bs2 = fp.getFingerprint(container);
            Assert.assertTrue(bs1.equals(bs2));
        }
    }

    @Test
    public void testAtomPermutation2() throws CDKException {
        IMolecule pamine = MoleculeFactory.makeCyclopentane();
        Fingerprinter fp = new Fingerprinter();
        BitSet bs1 = fp.getFingerprint(pamine);

        AtomContainerAtomPermutor acp = new AtomContainerAtomPermutor(pamine);
        while (acp.hasNext()) {
            IAtomContainer container = acp.next();
            BitSet bs2 = fp.getFingerprint(container);
            Assert.assertTrue(bs1.equals(bs2));
        }
    }

    public static Molecule makeFragment1()
	{
		Molecule mol = new Molecule();
		mol.addAtom(new Atom("C")); // 0
		mol.addAtom(new Atom("C")); // 1
		mol.addAtom(new Atom("C")); // 2
		mol.addAtom(new Atom("C")); // 3
		mol.addAtom(new Atom("C")); // 4
		mol.addAtom(new Atom("C")); // 5
		mol.addAtom(new Atom("C")); // 6

		mol.addBond(0, 1, IBond.Order.SINGLE); // 1
		mol.addBond(0, 2, IBond.Order.SINGLE); // 2
		mol.addBond(0, 3, IBond.Order.SINGLE); // 3
		mol.addBond(0, 4, IBond.Order.SINGLE); // 4
		mol.addBond(3, 5, IBond.Order.SINGLE); // 5
		mol.addBond(5, 6, IBond.Order.DOUBLE); // 6
		return mol;
	}


	public static Molecule makeFragment4()
	{
		Molecule mol = new Molecule();
		mol.addAtom(new Atom("C")); // 0
		mol.addAtom(new Atom("C")); // 1

		mol.addBond(0, 1, IBond.Order.SINGLE); // 1
		return mol;
	}

	public static Molecule makeFragment2()
	{
		Molecule mol = new Molecule();
		mol.addAtom(new Atom("C")); // 0
		mol.addAtom(new Atom("C")); // 1
		mol.addAtom(new Atom("C")); // 2
		mol.addAtom(new Atom("S")); // 3
		mol.addAtom(new Atom("O")); // 4
		mol.addAtom(new Atom("C")); // 5
		mol.addAtom(new Atom("C")); // 6

		mol.addBond(0, 1, IBond.Order.DOUBLE); // 1
		mol.addBond(0, 2, IBond.Order.SINGLE); // 2
		mol.addBond(0, 3, IBond.Order.SINGLE); // 3
		mol.addBond(0, 4, IBond.Order.SINGLE); // 4
		mol.addBond(3, 5, IBond.Order.SINGLE); // 5
		mol.addBond(5, 6, IBond.Order.DOUBLE); // 6
		mol.addBond(5, 6, IBond.Order.DOUBLE); // 7
		return mol;
	}

	public static Molecule makeFragment3()
	{
		Molecule mol = new Molecule();
		mol.addAtom(new Atom("C")); // 0
		mol.addAtom(new Atom("C")); // 1
		mol.addAtom(new Atom("C")); // 2
		mol.addAtom(new Atom("C")); // 3
		mol.addAtom(new Atom("C")); // 4
		mol.addAtom(new Atom("C")); // 5
		mol.addAtom(new Atom("C")); // 6

		mol.addBond(0, 1, IBond.Order.SINGLE); // 1
		mol.addBond(0, 2, IBond.Order.SINGLE); // 2
		mol.addBond(0, 3, IBond.Order.SINGLE); // 3
		mol.addBond(0, 4, IBond.Order.SINGLE); // 4
		mol.addBond(3, 5, IBond.Order.DOUBLE); // 5
		mol.addBond(5, 6, IBond.Order.SINGLE); // 6
		return mol;
	}

    public static Molecule makeButane() {
        Molecule mol = new Molecule();
		mol.addAtom(new Atom("C")); // 0
		mol.addAtom(new Atom("C")); // 1
		mol.addAtom(new Atom("C")); // 2
		mol.addAtom(new Atom("C")); // 3

		mol.addBond(0, 1, IBond.Order.SINGLE); // 1
		mol.addBond(1, 2, IBond.Order.SINGLE); // 2
		mol.addBond(2, 3, IBond.Order.SINGLE); // 3
		
		return mol;
    }

    public static Molecule makePropylAmine() {
        Molecule mol = new Molecule();
		mol.addAtom(new Atom("C")); // 0
		mol.addAtom(new Atom("C")); // 1
		mol.addAtom(new Atom("C")); // 2
		mol.addAtom(new Atom("N")); // 3

		mol.addBond(0, 1, IBond.Order.SINGLE); // 1
		mol.addBond(1, 2, IBond.Order.SINGLE); // 2
		mol.addBond(2, 3, IBond.Order.SINGLE); // 3

		return mol;
    }
 
    public static void main(String[] args) throws Exception
	{
		BigInteger bi=new BigInteger("0");
		bi=bi.add(BigInteger.valueOf((long) Math.pow(2, 63)));
		System.err.println(bi.toString());
		bi=bi.add(BigInteger.valueOf((long) Math.pow(2, 0)));
		System.err.println(bi.toString());
		FingerprinterTest fpt = new FingerprinterTest();
		fpt.standAlone = true;
		//fpt.testFingerprinter();
		//fpt.testFingerprinterArguments();
		//fpt.testBug706786();
		//fpt.testBug771485();
		//fpt.testBug853254();
		//fpt.testBug931608();
		fpt.testBug934819();
	}   
}

