/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 1997-2006  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
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
package org.openscience.cdk.test.renderer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.InputStream;
import java.util.Hashtable;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.renderer.Renderer2D;
import org.openscience.cdk.renderer.Renderer2DModel;

/**
 * @cdk.module test-extra
 */
public class Renderer2DTest extends JPanel
{
    private static final long serialVersionUID = -8921461176181084638L;
    MDLReader mr;
        CMLReader cr;
	ChemFile chemFile;
	org.openscience.cdk.interfaces.IChemSequence chemSequence;
	org.openscience.cdk.interfaces.IChemModel chemModel;
	org.openscience.cdk.interfaces.IMoleculeSet setOfMolecules;
	org.openscience.cdk.interfaces.IMolecule molecule;

	Renderer2DModel r2dm;
	Renderer2D renderer;

	public Renderer2DTest(String inFile)
	{
		Hashtable ht = null;
		r2dm = new Renderer2DModel();
		renderer = new Renderer2D(r2dm);
        Dimension screenSize = new Dimension(600, 400);
        setPreferredSize(screenSize);
        r2dm.setBackgroundDimension(screenSize); // make sure it is synched with the JPanel size
		setBackground(r2dm.getBackColor());


		try
		{
	        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(inFile);
			//FileInputStream fis = new FileInputStream(inFile);
			if (inFile.endsWith(".cml")) {
			    cr = new CMLReader(ins);
			    chemFile = (ChemFile)cr.read((ChemObject)new ChemFile());
			} else {
			    mr = new MDLReader(ins);
			    chemFile = (ChemFile)mr.read((ChemObject)new ChemFile());
			}
			ins.close();
			chemSequence = chemFile.getChemSequence(0);
			chemModel = chemSequence.getChemModel(0);
			setOfMolecules = chemModel.getMoleculeSet();
			molecule = setOfMolecules.getMolecule(0);
			ht = r2dm.getColorHash();
            r2dm.setDrawNumbers(true);
            IAtomContainer selected = new AtomContainer();
            selected.addAtom(molecule.getAtom(2));
            selected.addAtom(molecule.getAtom(3));
            selected.addAtom(molecule.getAtom(4));
            selected.addAtom(molecule.getAtom(5));
            selected.addAtom(molecule.getAtom(6));
            selected.addAtom(molecule.getAtom(7));
            r2dm.setSelectedPart(selected);
			ht.put(molecule.getAtom(2), Color.red);
			ht.put(molecule.getAtom(4), Color.red);
			GeometryTools.translateAllPositive(molecule,r2dm.getRenderingCoordinates());
			GeometryTools.scaleMolecule(molecule, getPreferredSize(), 0.8,r2dm.getRenderingCoordinates());
			GeometryTools.center(molecule, getPreferredSize(),r2dm.getRenderingCoordinates());
		
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
		}
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().add(this);
		frame.pack();
		frame.setVisible(true);

	}

	public void paint(Graphics g)
	{
		super.paint(g);
		renderer.paintMolecule(molecule, (Graphics2D)g,false,true);
	}

	/**
	 * The main method.
	 *
	 * @param   args    The Arguments from the commandline
	 */	public static void main(String[] args)
	{
		new Renderer2DTest("data/mdl/reserpine.mol");
	}
}
