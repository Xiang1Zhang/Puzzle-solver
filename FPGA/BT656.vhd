Library IEEE;
Use ieee.std_logic_1164.all;
Use ieee.numeric_std.all;

Entity BT656 is
port(
	i_DATAin: IN std_logic_vector(7 downto 0);
	clkin: IN std_logic;	
	HS: IN std_logic := '1';
	VS: IN std_logic := '1';
	DATA: OUT std_logic_vector(7 downto 0);
	Dclk: OUT std_logic;
	s_reset : in std_logic;	
	ITR : in std_logic;
	DEBUG	: out std_logic
);
End BT656;

Architecture A of BT656 is
	signal i_DATA : std_logic_vector(7 downto 0):=X"00" ;
Begin
DATA <= i_DATA;
Process(clkin, s_reset)
	variable stateV: integer range 0 to 2 := 0;
	variable CNT: integer;
	variable CNTR: integer;
	variable e_VS: std_logic := '1';
	variable e_HS: std_logic := '1';
	variable counter: integer := 0;
	variable stateH: integer range 0 to 2 := 0;
	variable count: integer := 0;
	variable zero : std_logic := '0'; 
	variable e_itr : std_logic := '0';

Begin
	if(s_reset='0') then
		stateV := 0;
		stateH := 0;
		e_VS := '1';
		e_HS := '1';
		counter := 0;
		count := 0;
		zero := '0';
		e_itr := '0';
		DEBUG<='0';
	elsif rising_edge(clkin) THEN
		if(e_itr='0' and itr='1') then -- Rising edge of ITR
			stateV := 0;
			stateH := 0;
			e_VS := '1';
			e_HS := '1';
			counter := 0;
			count := 0;
			zero := '0';
			e_itr :='1';
		elsif(e_itr='1' and itr='0') then -- Falling edge of ITR
			e_itr := '0';
		end if;
	Case stateV is
		when 0 =>	--------------wait for rising edge for VS
		Dclk <= '0';
		if(VS='0') THEN e_VS:='0'; end if;
		if(e_VS='0' and VS='1') THEN
			e_VS := '1';
			CNT := 0;
			stateV := stateV + 1;
		end if;

		when 1 =>	--------------wait for 17 HS rising edges
		if(HS='0') THEN e_HS:='0'; end if;
		if(e_HS='0' and HS='1') THEN
			e_HS := '1';
			Dclk <= '0';
			CNT := CNT + 1;
			if(CNT=10) THEN
				stateV := stateV + 1;
				CNT := 0;
			end if;
		end if;
		
		when 2 =>	-------------
		Dclk <= '0';
		if(counter=288) THEN
			stateV := 0;
		end if;
			Case stateH is
				when 0 =>	--------------wait for rising edge for HS
				if(HS='0') THEN e_HS:='0'; end if;
				if(e_HS='0' and HS='1') THEN
					e_HS := '1';
					CNTR := 0;
					StateH := StateH +1;
				end if;
		
				when 1 =>	--------------wait 282 clkin
					CNTR := CNTR + 1;
					if(CNTR=282) THEN
						stateH := stateH + 1;
						DEBUG <= '1';
						CNTR := 0;
					end if;

				when 2 =>	--------------read the even clk 720 times
					count := count + 1;
					if(count rem 2 = 1) THEN
						i_DATA <= i_DATAin;
						if(zero='0') then zero := '1'; else Dclk <= '1'; end if;
						if(CNTR=720) THEN
							counter := counter + 1;
							stateH := 0;
							CNTR := 0;
							zero := '0';
							DEBUG <= '0';
						else
							CNTR := CNTR + 1;		
						end if;
						
					end if;
			end case;
	end case;
	end if;
end process;
end architecture;
