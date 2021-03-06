import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import exception.PandaCannotInstanciate;
import exception.PandaExceptions;
import exception.PandaNameNotFound;
import exception.PandaNoData;
import exception.PandaNotSupported;
import exception.PandaOutOfBound;
import exception.TooManyDataException;
import exception.TooManyValueException;

@SuppressWarnings("rawtypes")
public class TestDataframe {
	private static String PATH;
	private static final int nbColonnes = 8;
	private static final int nbLignes = 8;
	private Dataframe d;
	private Object[][] data;

	private void remplirFile(int largeur, int hauteur, String PATH) throws IOException {
		FileWriter fw = new FileWriter(PATH);
		fw.append("FILE;VALUE1;VALUE2\n");
		for (int i = 0; i < hauteur - 1; i++) {
			for (int j = 1; j <= largeur; j++) {
				fw.append(i * j + ";");
			}
			fw.append("\n");
		}
		fw.close();
	}

	@Before
	public void setUpBefore() throws Exception {
		data = new Object[nbColonnes][nbLignes + 1];
		for (int i = 0; i < nbLignes; i++) {
			if (i == 0) {
				data[0][i] = "A";
				data[1][i] = "B";
				data[2][i] = "C";
				data[3][i] = "D";
				data[4][i] = "E";
				data[5][i] = "F";
				data[6][i] = "G";
				data[7][i] = "H";
			} else {
				data[0][i] = i;
				data[1][i] = Integer.toString(i);
				data[2][i] = i;
				data[3][i] = i * 10;
				data[4][i] = Integer.toString(i * 10);
				data[5][i] = (double) i * 10;
				data[6][i] = i * 100;
				data[7][i] = Integer.toString(i * 100);
			}
		}
		d = new Dataframe(data);
		PATH = "src/test/resources/test.csv";
	}

	@After
	public void tearDownAfter() throws Exception {
	}

	@Test
	public void testAjout() {
		Dataframe dataframe = new Dataframe();
		assertEquals(dataframe.type("1"), d.INTEGER);
		assertEquals(dataframe.type("listeString"), d.STRING);
		assertEquals(dataframe.type("0.1"), d.DOUBLE);
	}

	@Test(expected = TooManyDataException.class)
	public void testAjoutTropGrand() throws Exception {
		Dataframe dataframe = new Dataframe();
		remplirFile(PandaExceptions.MAX_DATA, PandaExceptions.MAX_VALUES - 1, PATH);
		dataframe.toTab(PATH);
	}

	@Test(expected = TooManyValueException.class)
	public void testAjoutTropLarge() throws Exception {
		Dataframe dataframe = new Dataframe();
		remplirFile(PandaExceptions.MAX_DATA - 1, PandaExceptions.MAX_VALUES, PATH);
		dataframe.toTab(PATH);
	}

	@Test
	public void testDataframe() {
		int k = 0, l;
		for (Series s : d.getDataframe()) {
			assertEquals(s.getName(), data[k][0]);
			l = 1;
			for (Object o : s.getColumn()) {
				assertEquals(o, data[k][l]);
				l++;
			}
			k++;
		}
	}
	
	@Test(expected = PandaNoData.class)
	public void testDataframeTableauNull() throws PandaExceptions {
		Object[][] tab = null;
		new Dataframe(tab);
	}
	
	@Test(expected = PandaNoData.class)
	public void testDataframeTableauVide() throws PandaExceptions {
		Object[][] tab = {{}, {}};
		new Dataframe(tab);
	}
	
	@Test(expected = PandaNotSupported.class)
	public void testDataframeTableauMauvaisType() throws PandaExceptions {
		Object[][] tab = {{"Tab1", 1.5f, 3.6f}};
		new Dataframe(tab);
	}

	@Test
	public void testDataframeTableauToutType() throws PandaExceptions {
		data[0][1] = null;
		data[1][1] = null;
		data[5][1] = null;
		data[5][2] = null;
		Dataframe d = new Dataframe(data);
		int k = 0, l;
		for (Series s : d.getDataframe()) {
			assertEquals(s.getName(), data[k][0]);
			l = 1;
			for (Object o : s.getColumn()) {
				assertEquals(o, data[k][l]);
				l++;
			}
			k++;
		}
	}
	
	@Test(expected = PandaCannotInstanciate.class)
	public void testDataframeTableauErreurTypeInteger() throws PandaExceptions {
		data[0][3] = 0.5;
		new Dataframe(data);
	}
	
	@Test(expected = PandaCannotInstanciate.class)
	public void testDataframeTableauErreurTypeString() throws PandaExceptions {
		data[1][3] = 1;
		new Dataframe(data);
	}
	
	@Test(expected = PandaNotSupported.class)
	public void testDataframeTableauErreurTypeDoubleNull() throws PandaExceptions {
		data[5][1] = null;
		data[5][2] = (float) 1;
		new Dataframe(data);
	}
	
	@Test(expected = PandaCannotInstanciate.class)
	public void testDataframeTableauErreurTypeDouble() throws PandaExceptions {
		data[5][2] = (float) 1;
		new Dataframe(data);
	}
	
	@Test
	public void testgetMaxSizeSeries() throws PandaExceptions {
		Object[][] tab1 = { { "Tab1", 1, 2, 3, 4 }, { "Tab2" } };
		Dataframe data1 = new Dataframe(tab1);
		assertEquals(4, data1.getMaxSizeSeries());

		Object[][] tab2 = { { "Tab1" }, { "Tab2", 2, 4, 5 } };
		Dataframe data2 = new Dataframe(tab2);
		assertEquals(3, data2.getMaxSizeSeries());

		Object[][] tab3 = { { "Tab1", 1, 2, 3, 4 }, { "Tab2", 2, 4, 5 } };
		Dataframe data3 = new Dataframe(tab3);
		assertEquals(4, data3.getMaxSizeSeries());

		Object[][] tab4 = { { "Tab1", 3, 4 }, { "Tab2", 2, 4, 5 } };
		Dataframe data4 = new Dataframe(tab4);
		assertEquals(3, data4.getMaxSizeSeries());

		Object[][] tab5 = { { "Tab1" }, { "Tab2" } };
		Dataframe data5 = new Dataframe(tab5);
		assertEquals(0, data5.getMaxSizeSeries());
	}

	@Test
	public void testGetName() {
		String[][] all = new String[201][3];
		String[] contenu = { "FILE", "VALUE1", "VALUE2" };
		all[0] = contenu;
		for (int i = 0; i < 200; i++) {
			String[] toAdd = { Integer.toString(i), Integer.toString(i * 2), Integer.toString(i * 3) };
			all[i + 1] = toAdd;
		}
		for (int i = 0; i < 3; i++) {
			assertEquals(contenu[i], d.getName(all, i));
		}
	}
	
	@Test
	public void testGroupBy() throws Exception {
	   for (int i = 1; i < nbLignes; i++) {
	       if (i > 0 && i < 3) data[0][i] = 1;
	       else if (i >= 3 && i < 5) data[0][i] = 20;
	       else if (i >= 5 && i < 7) data[0][i] = 300;
	       else if (i >= 7) data[0][i] = 20;
	   }
	   d = new Dataframe(data);
	   GroupBy dGroupBy = d.groupBy("A");
	   int nbLigne = 0;
	   for (Dataframe dataframe : dGroupBy.getAll()) {
	       assertEquals(nbColonnes, dataframe.getDataframe().size());
	       for (Series s : dataframe.getDataframe()) {
	            if (s.getName().equals("A")) {
	               Object ref = s.getColumn().get(0);
	               for (Object o : s.getColumn()) {
	                   nbLigne++;
	                   assertEquals(ref, o);
	               }
	           }
	       }
	   }
	   assertEquals(nbLignes, nbLigne);
	}
	
	@Test(expected = PandaExceptions.class)
	public void testGroupByWrongName() throws Exception {
	   for (int i = 1; i < nbLignes; i++) {
	       if (i > 0 && i < 3) data[0][i] = 1;
	       else if (i >= 3 && i < 5) data[0][i] = 20;
	       else if (i >= 5 && i < 7) data[0][i] = 300;
	       else if (i >= 7) data[0][i] = 20;
	   }
	   d = new Dataframe(data);
	   d.groupBy("INCONNU");
	}
	
	@Test
	public void testIsDouble() throws Exception {
		String s = "0";
		assertFalse(d.isDouble(s));
		s = "0.2";
		assertTrue(d.isDouble(s));
		s = "BLABLA";
		assertFalse(d.isDouble(s));
	}

	@Test
	public void testIsInt() throws Exception {
		String s = "0";
		assertTrue(d.isInt(s));
		s = "0,2";
		assertFalse(d.isInt(s));
		s = "Test";
		assertFalse(d.isInt(s));
	}

	@Test
	public void testToString() throws Exception {
		Object[][] tab1 = { { "Tab1", 1, 2, 3, 4 }, { "Tab2", 2, 4, 5 } };
		Dataframe data = new Dataframe(tab1);
		assertEquals("Index\t\tTab1\t\tTab2\t\t\n" + "[0]\t\t1\t\t2\t\t\n" + "[1]\t\t2\t\t4\t\t\n"
					+ "[2]\t\t3\t\t5\t\t\n" + "[3]\t\t4\t\t\t\t\n", data.toString());
	}

	@Test
	public void testToStringFirstLines() throws Exception {
		Object[][] tab1 = { { "Tab1", 1, 2, 3, 4 }, { "Tab2", 2, 4, 5 } };
		Dataframe data = new Dataframe(tab1);
		assertEquals("Index\t\tTab1\t\tTab2\t\t\n", data.toStringFirstLines(0));

		assertEquals("Index\t\tTab1\t\tTab2\t\t\n" + "[0]\t\t1\t\t2\t\t\n", data.toStringFirstLines(1));

		assertEquals("Index\t\tTab1\t\tTab2\t\t\n" + "[0]\t\t1\t\t2\t\t\n" + "[1]\t\t2\t\t4\t\t\n",
				data.toStringFirstLines(2));

		assertEquals("Index\t\tTab1\t\tTab2\t\t\n" + "[0]\t\t1\t\t2\t\t\n" + "[1]\t\t2\t\t4\t\t\n"
				+ "[2]\t\t3\t\t5\t\t\n" + "[3]\t\t4\t\t\t\t\n", data.toStringFirstLines(152));

		assertEquals("Index\t\tTab1\t\tTab2\t\t\n", data.toStringFirstLines(-1));
	}

	@Test
	public void testToStringLastLines() throws Exception {
		Object[][] tab1 = { { "Tab1", 1, 2, 3, 4 }, { "Tab2", 2, 4, 5 } };
		Dataframe data = new Dataframe(tab1);
		assertEquals("Index\t\tTab1\t\tTab2\t\t\n", data.toStringLastLines(0));

		assertEquals("Index\t\tTab1\t\tTab2\t\t\n" + "[3]\t\t4\t\t\t\t\n", data.toStringLastLines(1));

		assertEquals("Index\t\tTab1\t\tTab2\t\t\n" + "[2]\t\t3\t\t5\t\t\n" + "[3]\t\t4\t\t\t\t\n",
				data.toStringLastLines(2));

		assertEquals("Index\t\tTab1\t\tTab2\t\t\n" + "[0]\t\t1\t\t2\t\t\n" + "[1]\t\t2\t\t4\t\t\n"
				+ "[2]\t\t3\t\t5\t\t\n" + "[3]\t\t4\t\t\t\t\n", data.toStringLastLines(152));

		assertEquals("Index\t\tTab1\t\tTab2\t\t\n", data.toStringLastLines(-1));
	}

	@Test
	public void testSelection() throws Exception {
		Dataframe newData = d.selection(0);
		int k = 0;
		for (Series s : newData.getDataframe()) {
			assertEquals(s.getName(), data[k][0]);
			for (Object o : s.getColumn()) {
				assertEquals(o, data[k][1]);
			}
			k++;
		}
	}

	@Test
	public void testSelectionColonne() throws Exception {
		Dataframe newData;
		String[] tab = { "A", "B", "C" };
		int i = 0, j;
		Series s;
		for (String t : tab) {
			newData = d.selectionColonne(t);
			assertTrue(newData.getDataframe().size() == 1);
			s = newData.getDataframe().get(0);
			assertEquals(data[i][0], s.getName());
			j = 1;
			for (Object o : s.getColumn()) {
				assertEquals(data[i][j], o);
				j++;
			}
			i++;
		}
	}

	@Test(expected = PandaNameNotFound.class)
	public void testSelectionColonneNomInconnu() throws Exception {
		d.selectionColonne("Inconnu");
	}

	@Test
	public void testSelectionColonnes() throws Exception {
		ArrayList<String> selection = new ArrayList<>();
		selection.add("A");
		selection.add("E");
		selection.add("C");
		selection.add("H");
		int[] indices = { 0, 4, 2, 7 };
		Dataframe newData = d.selectionColonnes(selection);
		int i = 0, j;
		for (Series s : newData.getDataframe()) {
			assertEquals(data[indices[i]][0], s.getName());
			j = 1;
			for (Object o : s.getColumn()) {
				assertEquals(data[indices[i]][j], o);
				j++;
			}
			i++;
		}
	}

	@Test(expected = PandaNoData.class)
	public void testSelectionColonnesListNull() throws Exception {
		ArrayList<String> selection = null;
		d.selectionColonnes(selection);
	}

	@Test(expected = PandaNoData.class)
	public void testSelectionColonnesListVide() throws Exception {
		ArrayList<String> selection = new ArrayList<>();
		d.selectionColonnes(selection);
	}

	@Test(expected = PandaNameNotFound.class)
	public void testSelectionColonnesNomInconnu() throws Exception {
		ArrayList<String> selection = new ArrayList<>();
		selection.add("A");
		selection.add("Inconnu");
		selection.add("C");
		selection.add("H");
		d.selectionColonnes(selection);
	}

	@Test
	public void testSelectionElement() throws Exception {
		assertEquals(data[0][1], d.selectionElement(0, 0));
		assertEquals(data[1][3], d.selectionElement(2, 1));
		assertEquals(data[2][7], d.selectionElement(6, 2));
	}

	@Test(expected = PandaOutOfBound.class)
	public void testSelectionElementColonneNegatif() throws Exception {
		d.selectionElement(0, -1);
	}

	@Test(expected = PandaOutOfBound.class)
	public void testSelectionElementColonneTropGrand() throws Exception {
		d.selectionElement(0, nbColonnes);
	}

	@Test(expected = PandaOutOfBound.class)
	public void testSelectionElementLigneNegatif() throws Exception {
		d.selectionElement(-1, 0);
	}

	@Test(expected = PandaOutOfBound.class)
	public void testSelectionElementLigneTropGrand() throws Exception {
		d.selectionElement(nbLignes, 0);
	}

	@Test
	public void testSelectionElements() throws Exception {
		ArrayList<Integer> colonne = new ArrayList<>();
		ArrayList<Integer> ligne = new ArrayList<>();
		colonne.add(5);
		colonne.add(4);
		ligne.add(1);
		ligne.add(7);
		Dataframe newData = d.selectionElements(ligne, colonne);
		int k = 0, m;
		for (Series s : newData.getDataframe()) {
			assertEquals(data[colonne.get(k)][0], s.getName());
			m = 0;
			for (Object o : s.getColumn()) {
				assertEquals(data[colonne.get(k)][ligne.get(m) + 1], o);
				m++;
			}
			k++;
		}
	}

	@Test(expected = PandaOutOfBound.class)
	public void testSelectionElementsColonneNegatif() throws Exception {
		ArrayList<Integer> colonne = new ArrayList<>();
		ArrayList<Integer> ligne = new ArrayList<>();
		colonne.add(5);
		colonne.add(-1);
		ligne.add(1);
		d.selectionElements(ligne, colonne);
	}

	@Test(expected = PandaNoData.class)
	public void testSelectionElementsColonneNull() throws Exception {
		ArrayList<Integer> colonne = null;
		ArrayList<Integer> ligne = new ArrayList<>();
		ligne.add(5);
		d.selectionElements(ligne, colonne);
	}

	@Test(expected = PandaOutOfBound.class)
	public void testSelectionElementsColonneTropGrand() throws Exception {
		ArrayList<Integer> colonne = new ArrayList<>();
		ArrayList<Integer> ligne = new ArrayList<>();
		colonne.add(5);
		colonne.add(nbColonnes);
		ligne.add(1);
		d.selectionElements(ligne, colonne);
	}

	@Test(expected = PandaNoData.class)
	public void testSelectionElementsColonneVide() throws Exception {
		ArrayList<Integer> colonne = new ArrayList<>();
		ArrayList<Integer> ligne = new ArrayList<>();
		ligne.add(5);
		d.selectionElements(ligne, colonne);
	}

	@Test(expected = PandaOutOfBound.class)
	public void testSelectionElementsLigneNegatif() throws Exception {
		ArrayList<Integer> colonne = new ArrayList<>();
		ArrayList<Integer> ligne = new ArrayList<>();
		colonne.add(5);
		colonne.add(4);
		ligne.add(1);
		ligne.add(-1);
		d.selectionElements(ligne, colonne);
	}

	@Test(expected = PandaNoData.class)
	public void testSelectionElementsLigneNull() throws Exception {
		ArrayList<Integer> colonne = new ArrayList<>();
		ArrayList<Integer> ligne = null;
		colonne.add(5);
		colonne.add(2);
		d.selectionElements(ligne, colonne);
	}

	@Test(expected = PandaOutOfBound.class)
	public void testSelectionElementsLigneTropGrand() throws Exception {
		ArrayList<Integer> colonne = new ArrayList<>();
		ArrayList<Integer> ligne = new ArrayList<>();
		colonne.add(5);
		colonne.add(4);
		ligne.add(nbLignes);
		ligne.add(5);
		d.selectionElements(ligne, colonne);
	}

	@Test(expected = PandaNoData.class)
	public void testSelectionElementsLigneVide() throws Exception {
		ArrayList<Integer> colonne = new ArrayList<>();
		ArrayList<Integer> ligne = new ArrayList<>();
		colonne.add(5);
		colonne.add(2);
		d.selectionElements(ligne, colonne);
	}

	@Test(expected = PandaOutOfBound.class)
	public void testSelectionIndexNegatif() throws Exception {
		d.selection(-1);
	}

	@Test(expected = PandaOutOfBound.class)
	public void testSelectionIndexTropGrand() throws Exception {
		d.selection(nbLignes + 1);
	}

	@Test
	public void testSelectionLignes() throws Exception {
		ArrayList<Integer> selection = new ArrayList<>();
		selection.add(0);
		selection.add(3);
		selection.add(5);
		selection.add(6);
		Dataframe newData = d.selectionLignes(selection);
		int i = 0, j;
		for (Series s : newData.getDataframe()) {
			j = 0;
			assertEquals(data[i][0], s.getName());
			for (Object o : s.getColumn()) {
				assertEquals(data[i][selection.get(j) + 1], o);
				j++;
			}
			i++;
		}
	}

	@Test(expected = PandaOutOfBound.class)
	public void testSelectionLignesIndexNegatif() throws Exception {
		ArrayList<Integer> selection = new ArrayList<>();
		selection.add(0);
		selection.add(-1);
		selection.add(7);
		selection.add(8);
		d.selectionLignes(selection);
	}

	@Test(expected = PandaOutOfBound.class)
	public void testSelectionLignesIndexTropGrand() throws Exception {
		ArrayList<Integer> selection = new ArrayList<>();
		selection.add(0);
		selection.add(nbLignes + 1);
		selection.add(7);
		selection.add(8);
		d.selectionLignes(selection);
	}

	@Test(expected = PandaNoData.class)
	public void testSelectionLignesListeNulle() throws Exception {
		ArrayList<Integer> ligne = null;
		d.selectionLignes(ligne);
	}

	@Test(expected = PandaNoData.class)
	public void testSelectionLignesListeVide() throws Exception {
		ArrayList<Integer> ligne = new ArrayList<>();
		d.selectionLignes(ligne);
	}

	@Test
	public void testSelectionMasque() throws Exception {
		ArrayList<Boolean> tab = new ArrayList<>();
		for (int i = 0; i < nbLignes; i++)
			tab.add(false);
		tab.set(1, true);
		tab.set(5, true);
		tab.set(6, true);
		int i = 0, j, k;
		Dataframe newData = d.selectionMasque(tab);
		for (Series s : newData.getDataframe()) {
			assertEquals(data[i][0], s.getName());
			j = 1;
			k = 0;
			for (Boolean b : tab) {
				if (b) {
					assertEquals(data[i][j], s.getElem(k));
					k++;
				}
				j++;
			}
			i++;
		}
	}

	@Test(expected = PandaNoData.class)
	public void testSelectionMasqueNull() throws Exception {
		ArrayList<Boolean> tab = null;
		d.selectionMasque(tab);
	}

	@Test(expected = PandaNoData.class)
	public void testSelectionMasqueTailleDifferente() throws Exception {
		ArrayList<Boolean> tab = new ArrayList<>();
		tab.add(false);
		d.selectionMasque(tab);
	}

	@Test(expected = PandaNoData.class)
	public void testSelectionMasqueVide() throws Exception {
		ArrayList<Boolean> tab = new ArrayList<>();
		d.selectionMasque(tab);
	}
	
	@Test
	public void testSelectionParObjet() throws Exception {
		FileWriter fw = new FileWriter(PATH);
		fw.append("FILE;VALUE1;VALUE2\n");
		for (int i = 0; i < 50; i++) {
			for (int j = 1; j <= 3; j++) {
				fw.append(i % 3 + ";");
			}
			fw.append("\n");
		}
		fw.close();
		Dataframe d = new Dataframe(PATH);
		Dataframe newData;
		newData = d.selectionParObjet("VALUE1", 0);
		for (Series s : newData.getDataframe()) {
			for (Object o : s.getColumn())
				assertEquals(0, o);
			assertEquals(s.getSize(), 51 / 3);
		}
		newData = d.selectionParObjet("VALUE1", 1);
		for (Series s : newData.getDataframe()) {
			for (Object o : s.getColumn()) {
				assertEquals(1, o);
			}
		}
		newData = d.selectionParObjet("VALUE1", 2);
		for (Series s : newData.getDataframe()) {
			for (Object o : s.getColumn()) {
				assertEquals(2, o);
			}
		}
	}
	
	@Test(expected = PandaNameNotFound.class)
	public void testSelectionParObjetNomInconnu() throws Exception {
		d.selectionParObjet("Inconnu", 0);
	}
	
	@Test
	public void testSelectionParObjetAllType() throws Exception {
		FileWriter fw = new FileWriter("src/test/resources/testType.csv");
		fw.append("FILE;VALUE1;VALUE2\n");
		for (int i = 0; i < 50; i++) {
			fw.append(i % 3 + ";");
			fw.append("txt;");
			fw.append((i % 3) + 0.5 + ";");
			fw.append("\n");
		}
		fw.close();
		Dataframe d = new Dataframe("src/test/resources/testType.csv");
		Dataframe newData;
		newData = d.selectionParObjet("VALUE1", "txt");
		int j = 0;
		for (int i = 0; i < newData.getDataframe().size(); i++) {
			Series s = newData.getDataframe().get(i);
			if(i==0) {
				for (Object o : s.getColumn()) {
					assertEquals(j%3, o);
					j++;
				}
			}
			else if(i==1) {
				for (Object o : s.getColumn()) {
					assertEquals("txt", o);
					j++;
				}
			}
			else if(i==2) {
				for (Object o : s.getColumn()) {
					assertEquals((j%3) + 0.5, o);
					j++;
				}
			}
			j=0;
		}
		newData = d.selectionParObjet("VALUE2", 0.5);
		j = 0;
		for (int i = 0; i < newData.getDataframe().size(); i++) {
			Series s = newData.getDataframe().get(i);
			if(i==0) {
				for (Object o : s.getColumn()) {
					assertEquals(0, o);
					j++;
				}
			}
			else if(i==1) {
				for (Object o : s.getColumn()) {
					assertEquals("txt", o);
					j++;
				}
			}
			else if(i==2) {
				for (Object o : s.getColumn()) {
					assertEquals(0.5, o);
					j++;
				}
			}
			j=0;
		}
	}
	
	@Test
	public void testCreationDataframeCSVallType() throws Exception {
		FileWriter fw = new FileWriter("src/test/resources/testType.csv");
		fw.append("FILE;VALUE1;VALUE2\n");
		for (int i = 0; i < 50; i++) {
			fw.append(i % 3 + ";");
			fw.append("txt;");
			fw.append((i % 3) + 0.5 + ";");
			fw.append("\n");
		}
		fw.close();
		new Dataframe("src/test/resources/testType.csv");
	}
	
	@Test
	public void testToTab() throws Exception {
		Dataframe dataframe = new Dataframe();
		int hauteur = 201;
		int largeur = 3;
		remplirFile(largeur, hauteur, PATH);
		String[][] all = new String[201][3];
		String[] contenu = { "FILE", "VALUE1", "VALUE2" };
		all[0] = contenu;
		for (int i = 0; i < 200; i++) {
			String[] toAdd = { Integer.toString(i), Integer.toString(i * 2), Integer.toString(i * 3) };
			all[i + 1] = toAdd;
		}

		String[][] tab = dataframe.toTab(PATH);
		for (int i = 0; i < 201; i++) {
			for (int j = 0; j < 3; j++) {
				assertEquals(" verification a l'indice " + i + "" + j + ".", all[i][j], tab[i][j]);
			}
		}
	}

	// Test methodes MeanEachColumn & MeanColumn
	
	@Test
	public void testMeanCol() throws Exception {
		Object[][] tab1 = { { "Tab1", 1, 2, 3, 4 } };
		Dataframe data1 = new Dataframe(tab1);
		assertEquals(2.5, (double) data1.getMeanColumn(0), 0);
	}
	
	@Test
	public void testMeanColUpperIndex() throws Exception {
		Object[][] tab1 = { { "Tab1", 1, 2, 3, 4 } };
		Dataframe data1 = new Dataframe(tab1);
		assertEquals("NaN", (String) data1.getMeanColumn(100));
	}
	
	@Test
	public void testMeanColString() throws Exception {
		Object[][] tab1 = { { "Tab1", "1", "2", "3", "4" } };
		Dataframe data1 = new Dataframe(tab1);
		assertEquals("NaN", (String) data1.getMeanColumn(0));
	}

	@Test
	public void testMeanColZero() throws Exception {
		Object[][] tab1 = { { "Tab1", 0, 0, 0, 0 } };
		Dataframe data1 = new Dataframe(tab1);
		assertEquals(0.0, (double) data1.getMeanColumn(0), 0);
	}

	@Test
	public void testMeanColNeg() throws Exception {
		Object[][] tab1 = { { "Tab1", -2, -1, -4, -3 } };
		Dataframe data1 = new Dataframe(tab1);
		assertEquals(-2.5, (double) data1.getMeanColumn(0), 0);
	}

	@Test
	public void testMeanAll() throws Exception {
		Object[][] tab1 = { { "Tab1", -2, -1, 0, 1, 2 } };
		Dataframe data1 = new Dataframe(tab1);
		assertEquals(0.0, (double) data1.getMeanColumn(0), 0);
	}

	@Test
	public void testMeanEachCol() throws Exception {
		Object[][] tab1 = { { "Tab1", -2, -1, 0, 1, 2 }, { "Tab2", 0.5, 178.6, 1.65, 1.2 } , { "Tab4", "1", "2"} };
		Dataframe data1 = new Dataframe(tab1);
		ArrayList<Series> res = data1.getMeanEachColumn().getDataframe();
		assertEquals(0.0, (double) res.get(0).getElem(0), 0);
		assertEquals(45.4875, (double) res.get(1).getElem(0), 0.0001);
		assertEquals("NaN", (String) res.get(2).getElem(0));
	}

	@Test
	public void testMeanEachColVide() throws Exception {
		Object[][] tab1 = { { "Tab1" } };
		Dataframe data1 = new Dataframe(tab1);
		ArrayList<Series> res = data1.getMeanEachColumn().getDataframe();
		assertEquals("null", res.get(0).getElem(0));
	}

	// Test methodes MaxEachColumn & MaxColumn

	@Test
	public void testMaxCol() throws Exception {
		Object[][] tab1 = { { "Tab1", 1, 2, 3, 4 } };
		Dataframe data1 = new Dataframe(tab1);
		assertEquals(4.0, (double) data1.getMaxColumn(0), 0);
	}

	@Test
	public void testMaxColUpperIndex() throws Exception {
		Object[][] tab1 = { { "Tab1", 1, 2, 3, 4 } };
		Dataframe data1 = new Dataframe(tab1);
		assertEquals("NaN", (String) data1.getMaxColumn(100));
	}
	
	@Test
	public void testMaxColString() throws Exception {
		Object[][] tab1 = { { "Tab1", "1", "2", "3", "4" } };
		Dataframe data1 = new Dataframe(tab1);
		assertEquals("NaN", (String) data1.getMaxColumn(0));
	}
	
	@Test
	public void testMaxColZero() throws Exception {
		Object[][] tab1 = { { "Tab1", 0, 0, 0, 0 } };
		Dataframe data1 = new Dataframe(tab1);
		assertEquals(0.0, (double) data1.getMaxColumn(0), 0);
	}

	@Test
	public void testMaxColNeg() throws Exception {
		Object[][] tab1 = { { "Tab1", -2, -1, -4, -3 } };
		Dataframe data1 = new Dataframe(tab1);
		assertEquals(-1.0, (double) data1.getMaxColumn(0), 0);
	}

	@Test
	public void testMaxAll() throws Exception {
		Object[][] tab1 = { { "Tab1", -2, -1, 0, 1, 2 } };
		Dataframe data1 = new Dataframe(tab1);
		assertEquals(2.0, (double) data1.getMaxColumn(0), 0);
	}

	@Test
	public void testMaxEachCol() throws Exception {
		Object[][] tab1 = { { "Tab1", -2, -1, 0, 1, 2 }, { "Tab2", 0.5, 178.6, 1.65, 1.2 },
				{ "Tab3", -0.5, -178.6, -1.65, -1.2 }, { "Tab4", "1", "2"} };
		Dataframe data1 = new Dataframe(tab1);
		ArrayList<Series> res = data1.getMaxEachColumn().getDataframe();
		assertEquals(2.0, (double) res.get(0).getElem(0), 0);
		assertEquals(178.6, (double) res.get(1).getElem(0), 0);
		assertEquals(-0.5, (double) res.get(2).getElem(0), 0);
		assertEquals("NaN", (String) res.get(3).getElem(0));
	}

	@Test
	public void testMaxEachColVide() throws Exception {
		Object[][] tab1 = { { "Tab1" } };
		Dataframe data1 = new Dataframe(tab1);
		ArrayList<Series> res = data1.getMaxEachColumn().getDataframe();
		assertEquals("null", res.get(0).getElem(0));
	}
	
	// Test methodes MinEachColumn & MinColumn
	
	@Test
	public void testMinCol() throws Exception {
		Object[][] tab1 = { { "Tab1", 1, 2, 3, 4 } };
		Dataframe data1 = new Dataframe(tab1);
		assertEquals(1.0, (double) data1.getMinColumn(0), 0);
	}
	
	@Test
	public void testMinColUpperIndex() throws Exception {
		Object[][] tab1 = { { "Tab1", 1, 2, 3, 4 } };
		Dataframe data1 = new Dataframe(tab1);
		assertEquals("NaN", (String) data1.getMinColumn(100));
	}
	
	@Test
	public void testMinColString() throws Exception {
		Object[][] tab1 = { { "Tab1", "1", "2", "3", "4" } };
		Dataframe data1 = new Dataframe(tab1);
		assertEquals("NaN", (String) data1.getMinColumn(0));
	}

	@Test
	public void testMinColZero() throws Exception {
		Object[][] tab1 = { { "Tab1", 0, 0, 0, 0 } };
		Dataframe data1 = new Dataframe(tab1);
		assertEquals(0.0, (double) data1.getMinColumn(0), 0);
	}

	@Test
	public void testMinColNeg() throws Exception {
		Object[][] tab1 = { { "Tab1", -2, -1, -4, -3 } };
		Dataframe data1 = new Dataframe(tab1);
		assertEquals(-4.0, (double) data1.getMinColumn(0), 0);
	}

	@Test
	public void testMinAll() throws Exception {
		Object[][] tab1 = { { "Tab1", -2, -1, 0, 1, 2 } };
		Dataframe data1 = new Dataframe(tab1);
		assertEquals(-2.0, (double) data1.getMinColumn(0), 0);
	}
	
	@Test
	public void testgetMinEachCol() throws Exception {
		Object[][] tab1 = { { "Tab1", -2, -1, 0, 1, 2 }, { "Tab2", 0.5, 178.6, 1.65, 1.2 },
				{ "Tab3", -0.5, -178.6, -1.65, -1.2 }, { "Tab4", "1", "2"} };
		Dataframe data1 = new Dataframe(tab1);
		ArrayList<Series> res = data1.getMinEachColumn().getDataframe();
		assertEquals(-2.0, (double) res.get(0).getElem(0), 0);
		assertEquals(0.5, (double) res.get(1).getElem(0), 0);
		assertEquals(-178.6, (double) res.get(2).getElem(0), 0);
		assertEquals("NaN", (String) res.get(3).getElem(0));
	}
	
	@Test
	public void testMinEachColVide() throws Exception {
		Object[][] tab1 = { { "Tab1" } };
		Dataframe data1 = new Dataframe(tab1);
		ArrayList<Series> res = data1.getMinEachColumn().getDataframe();
		assertEquals("null", res.get(0).getElem(0));
	}

	// Test methodes SdEachColumn & SdColumn
	
	@Test
	public void testSdCol() throws Exception {
		Object[][] tab1 = { { "Tab1", 1, 2, 3, 4 } };
		Dataframe data1 = new Dataframe(tab1);
		assertEquals(1.87, (double) data1.getSdColumn(0), 0);
	}

	@Test
	public void testSdColUpperIndex() throws Exception {
		Object[][] tab1 = { { "Tab1", 1, 2, 3, 4 } };
		Dataframe data1 = new Dataframe(tab1);
		assertEquals("NaN", (String) data1.getSdColumn(100));
	}
	
	@Test
	public void testSdColString() throws Exception {
		Object[][] tab1 = { { "Tab1", "1", "2", "3", "4" } };
		Dataframe data1 = new Dataframe(tab1);
		assertEquals("NaN", (String) data1.getSdColumn(0));
	}
	
	@Test
	public void testSdColZero() throws Exception {
		Object[][] tab1 = { { "Tab1", 0, 0, 0, 0 } };
		Dataframe data1 = new Dataframe(tab1);
		assertEquals(0.0, (double) data1.getSdColumn(0), 0);
	}

	@Test
	public void testSdColNeg() throws Exception {
		Object[][] tab1 = { { "Tab1", -2, -1, -4, -3 } };
		Dataframe data1 = new Dataframe(tab1);
		assertEquals(1.87, (double) data1.getSdColumn(0), 0);
	}

	@Test
	public void testSdAll() throws Exception {
		Object[][] tab1 = { { "Tab1", -2, -1, 0, 1, 2 } };
		Dataframe data1 = new Dataframe(tab1);
		assertEquals(2.45, (double) data1.getSdColumn(0), 0);
	}
	
	@Test
	public void testgetSdEachCol() throws Exception {
		Object[][] tab1 = { { "Tab1", -2, -1, 0, 1, 2 }, { "Tab2", 0.5, 178.6, 1.65, 1.2 },
				{ "Tab3", -0.5, -178.6, -1.65, -1.2 }, { "Tab4", "1", "2"} };
		Dataframe data1 = new Dataframe(tab1);
		ArrayList<Series> res = data1.getSdEachColumn().getDataframe();
		assertEquals(2.45, (double) res.get(0).getElem(0), 0);
		assertEquals(89.05, (double) res.get(1).getElem(0), 0);
		assertEquals(153.71, (double) res.get(2).getElem(0), 0);
		assertEquals("NaN", (String) res.get(3).getElem(0));
	}
	
	@Test
	public void testSdEachColVide() throws Exception {
		Object[][] tab1 = { { "Tab1" } };
		Dataframe data1 = new Dataframe(tab1);
		ArrayList<Series> res = data1.getSdEachColumn().getDataframe();
		assertEquals("null", res.get(0).getElem(0));
	}

	@Test
	public void testSplitSortedPercent() throws Exception {
		Object[][] tab1 = { { "Tab1", 5,9,1,2,3,7,8,4,6,0 } };
		Dataframe data1 = new Dataframe(tab1);
		Dataframe newData1 = data1.splitPercent_SortedData(34);
		ArrayList<Series> res = data1.getDataframe();
		ArrayList<Series> newRes = newData1.getDataframe();
		assertEquals(res.get(0).getElem(9), newRes.get(0).getElem(0));
		assertEquals(res.get(0).getElem(2), newRes.get(0).getElem(1));
		assertEquals(res.get(0).getElem(3), newRes.get(0).getElem(2));
		assertEquals(res.get(0).getElem(5), newRes.get(1).getElem(0));
		assertEquals(res.get(0).getElem(6), newRes.get(1).getElem(1));
		assertEquals(res.get(0).getElem(1), newRes.get(1).getElem(2));
	}
	
	@Test
	public void testSplitSortedPercentLowerOfRange() throws Exception {
		Object[][] tab1 = { { "Tab1", 5,9,1,2,3,7,8,4,6,0 } };
		Dataframe data1 = new Dataframe(tab1);
		Dataframe newData1 = data1.splitPercent_SortedData(-8);
		ArrayList<Series> newRes = newData1.getDataframe();
		assertEquals(0, newRes.get(0).getSize());
	}
	
	@Test
	public void testSplitSortedPercentHigherOfRange() throws Exception {
		Object[][] tab1 = { { "Tab1", 5,9,1,2,3,7,8,4,6,0 } };
		Dataframe data1 = new Dataframe(tab1);
		Dataframe newData1 = data1.splitPercent_SortedData(500);
		ArrayList<Series> newRes = newData1.getDataframe();
		assertEquals(0, newRes.get(0).getSize());
	}
}
