/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.text;

/**
 * Symbols for the heuristic ruby scanner.
 * 
 * @since 3.0
 */
public interface Symbols {
	// FIXME Add More Ruby keywords!
	int TokenEOF= -1;
	int TokenLBRACE= 1;
	int TokenRBRACE= 2;
	int TokenLBRACKET= 3;
	int TokenRBRACKET= 4;
	int TokenLPAREN= 5;
	int TokenRPAREN= 6;
	int TokenSEMICOLON= 7;
	int TokenOTHER= 8;
	int TokenCOLON= 9;
	int TokenQUESTIONMARK= 10;
	int TokenCOMMA= 11;
	int TokenEQUAL= 12;
	int TokenLESSTHAN= 13;
	int TokenGREATERTHAN= 14;
	int TokenIF= 109;
	int TokenDO= 1010;
	int TokenFOR= 1011;
	int TokenBEGIN= 1012;
	int TokenEND= 1013;
	int TokenCASE= 1014;
	int TokenELSE= 1015;
	int TokenBREAK= 1016;
	int TokenRESCUE= 1017;
	int TokenWHILE= 1018;
	int TokenRETURN= 1019;
	int TokenSWITCH= 1020;
	int TokenUNLESS= 1021;
	int TokenCLASS= 1026;
	int TokenMODULE= 1027;
	int TokenIDENT= 2000;
}
